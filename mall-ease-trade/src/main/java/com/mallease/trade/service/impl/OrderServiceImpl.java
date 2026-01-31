package com.mallease.trade.service.impl;

import com.mallease.common.enums.OrderStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.RedisService;
import com.mallease.trade.dao.OrderDao;
import com.mallease.trade.dao.OrderItemDao;
import com.mallease.trade.feign.ProductFeignClient;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.client.vo.OrderConfirmVO;
import com.mallease.trade.model.client.vo.OrderItemVO;
import com.mallease.trade.model.data.entity.CartItem;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
import com.mallease.trade.service.CartItemService;
import com.mallease.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final CartItemService cartItemService;
    private final RedisService redisService;
    private final ProductFeignClient productFeignClient;

    private static final String SNAPSHOT_KEY_PREFIX = "order:snapshot:";
    private static final long SNAPSHOT_EXPIRE_SECONDS = 30 * 60;

    @Override
    public OrderConfirmVO generateSnapshot(Long userId, List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new ApiException("请选择要结算的商品");
        }

        List<CartItem> cartItems = cartItemService.listByIds(cartItemIds);
        List<CartItem> userItems = cartItems.stream()
                .filter(item -> item.getUserId().equals(userId))
                .toList();

        if (userItems.isEmpty()) {
            throw new ApiException("购物车商品不存在或已失效");
        }

        String requestId = UUID.randomUUID().toString().replace("-", "");

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemVO> items = new ArrayList<>();

        for (CartItem cart : userItems) {
            BigDecimal subtotal = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItemVO item = OrderItemVO.builder()
                    .spuId(cart.getSpuId())
                    .skuId(cart.getSkuId())
                    .spuName(cart.getSpuName())
                    .skuPic(cart.getSkuPic())
                    .skuAttrs(cart.getSkuAttrs())
                    .price(cart.getPrice())
                    .quantity(cart.getQuantity())
                    .subtotal(subtotal)
                    .build();
            items.add(item);
        }

        OrderConfirmVO snapshot = OrderConfirmVO.builder()
                .requestId(requestId)
                .userId(userId)
                .items(items)
                .totalAmount(totalAmount)
                .freightAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .payAmount(totalAmount)
                .createTime(LocalDateTime.now())
                .build();

        String key = SNAPSHOT_KEY_PREFIX + requestId;
        redisService.set(key, snapshot, SNAPSHOT_EXPIRE_SECONDS);

        return snapshot;
    }

    @Override
    public OrderConfirmVO getSnapshot(String requestId) {
        String key = SNAPSHOT_KEY_PREFIX + requestId;
        return (OrderConfirmVO) redisService.get(key);
    }

    @Override
    public void deleteSnapshot(String requestId) {
        String key = SNAPSHOT_KEY_PREFIX + requestId;
        redisService.del(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submit(Long userId, Order order, String requestId) {
        OrderConfirmVO snapshot = getSnapshot(requestId);
        if (snapshot == null) {
            throw new ApiException("订单已过期，请重新结算");
        }

        if (!snapshot.getUserId().equals(userId)) {
            throw new ApiException("非法请求");
        }

        List<OrderItemVO> snapshotItems = snapshot.getItems();
        if (snapshotItems == null || snapshotItems.isEmpty()) {
            throw new ApiException("未选择任何商品");
        }

        Map<Long, Integer> skuMap = snapshotItems.stream().collect(Collectors.toMap(OrderItemVO::getSkuId, OrderItemVO::getQuantity));

        // 锁定库存
        productFeignClient.lockStock(skuMap);

        order.setOrderNo(orderNumberGenerator(userId));
        order.setRequestId(requestId);
        order.setUserId(userId);
        order.setTotalAmount(snapshot.getTotalAmount());
        order.setFreightAmount(snapshot.getFreightAmount());
        order.setDiscountAmount(snapshot.getDiscountAmount());
        order.setPayAmount(snapshot.getPayAmount());
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());

        List<OrderItem> orderItems = snapshotItems.stream()
                .map(item -> OrderItem.builder()
                        .spuId(item.getSpuId())
                        .skuId(item.getSkuId())
                        .spuName(item.getSpuName())
                        .skuPic(item.getSkuPic())
                        .skuAttrs(item.getSkuAttrs())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        OrderAggregate aggregate = OrderAggregate.builder()
                .order(order)
                .items(orderItems)
                .build();

        String orderNo = create(aggregate);

        deleteSnapshot(requestId);

        log.info("订单创建成功, requestId=, orderNo={}, userId={}", requestId, orderNo, userId);
        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(OrderAggregate aggregate) {
        Order order = aggregate.getOrder();
        List<OrderItem> orderItems = aggregate.getItems();

        if (orderItems == null || orderItems.isEmpty()) {
            throw new ApiException("未选择商品！");
        }

        orderDao.insert(order);

        orderItems.forEach(item -> {
            item.setOrderId(order.getId());
            item.setOrderNo(order.getOrderNo());
        });

        orderItemDao.insertBatch(orderItems);

        return order.getOrderNo();
    }

    @Override
    public OrderAggregate getByOrderNo(String orderNo) {
        Order order = orderDao.selectByOrderNo(orderNo);
        List<OrderItem> orderItems = orderItemDao.selectByOrderNo(orderNo);
        return OrderAggregate.builder().order(order).items(orderItems).build();
    }

    @Override
    public List<OrderAggregate> listByUserId(Long userId, Integer status) {
        List<Order> orders = orderDao.listByUserId(userId, status);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> orderItems = orderItemDao.selectByOrderIds(orderIds);
        Map<Long, List<OrderItem>> itemsMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        return orders.stream().map(order -> OrderAggregate.builder()
                .order(order)
                .items(itemsMap.get(order.getId()))
                .build()).toList();
    }

    @Override
    public boolean cancel(String orderNo, Long userId) {
        Order order = orderDao.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ApiException("无权操作此订单");
        }
        return orderDao.updateStatusByOrderNo(orderNo, OrderStatus.CANCELLED.getCode()) > 0;
    }

    private String orderNumberGenerator(Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String timestamp = LocalDateTime.now().format(formatter);
        String userIdSuffix = String.format("%04d", userId % 10000);
        String randomSuffix = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return timestamp + userIdSuffix + randomSuffix;
    }
}
