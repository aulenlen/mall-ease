package com.mallease.trade.service.impl;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.StockLockDTO;
import com.mallease.common.enums.OrderStatus;
import com.mallease.common.enums.StockReleaseStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.RedisService;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.common.util.NoGeneratorUtil;
import com.mallease.trade.dao.CartItemDao;
import com.mallease.trade.dao.OrderDao;
import com.mallease.trade.dao.OrderItemDao;
import com.mallease.trade.evnent.OrderCancelledEvent;
import com.mallease.trade.feign.ProductFeignClient;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.client.query.OrderQuery;
import com.mallease.trade.model.client.vo.OrderConfirmVO;
import com.mallease.trade.model.client.vo.OrderItemVO;
import com.mallease.trade.model.client.vo.OrderStatusDistributionVO;
import com.mallease.trade.model.client.vo.OrderStatsTrendVO;
import com.mallease.trade.model.data.entity.CartItem;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
import com.mallease.trade.service.CartItemService;
import com.mallease.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mallease.trade.constant.OrderConstant.PAYMENT_TIMEOUT_MINUTES;
import static com.mallease.trade.constant.OrderConstant.SNAPSHOT_EXPIRE_SECONDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartItemDao cartItemDao;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final CartItemService cartItemService;
    private final RedisService redisService;
    private final ProductFeignClient productFeignClient;
    private final ApplicationEventPublisher eventPublisher;

    private static final String SNAPSHOT_KEY_PREFIX = "order:snapshot:";

    @Override
    public OrderConfirmVO generateSnapshot() {
        Long userId = LoginContextUtil.getUserId();
        List<CartItem> userItems = cartItemService.listCheckedByUserId(userId);
        if (userItems.isEmpty()) {
            throw new ApiException("请选择要结算的商品");
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
    public List<Order> listNeedProcess() {
        return orderDao.listNeedProcess(
                OrderStatus.PENDING_PAYMENT.getCode(),
                LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES),
                OrderStatus.CANCELLED.getCode(),
                StockReleaseStatus.PENDING_RELEASE.getCode(),
                StockReleaseStatus.RELEASE_FAILED.getCode()
        );
    }

    @Override
    public int orderReleaseSuccess(List<String> released, boolean restoreCart) {
        int updated = orderDao.updateStockReleaseStatusByOrderNos(released, StockReleaseStatus.RELEASED.getCode());
        if (restoreCart) {
            refillCartItems(released);
        }
        return updated;
    }

    @Override
    public int orderReleaseFailed(List<String> failed) {
        return orderDao.updateStockReleaseStatusByOrderNos(failed, StockReleaseStatus.RELEASE_FAILED.getCode());
    }

    @Override
    public Order findPending(Long userId, String orderNo) {
        return orderDao.selectByConditions(userId, orderNo, OrderStatus.PENDING_PAYMENT.getCode());
    }

    @Override
    public int updateStatus(String orderNo, int status) {

        return orderDao.updateStatusByOrderNo(orderNo, status);
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

        Order existOrder = orderDao.selectByRequestId(requestId);
        if (existOrder != null) {
            log.info("订单已存在，返回已有订单号: requestId={}, orderNo={}", requestId, existOrder.getOrderNo());
            return existOrder.getOrderNo();
        }

        List<OrderItemVO> snapshotItems = snapshot.getItems();
        if (snapshotItems == null || snapshotItems.isEmpty()) {
            throw new ApiException("未选择任何商品");
        }

        Map<Long, Map<Long, Integer>> spuSkuQuantityMap = snapshotItems.stream()
                .collect(Collectors.groupingBy(
                        OrderItemVO::getSpuId,
                        Collectors.toMap(OrderItemVO::getSkuId, OrderItemVO::getQuantity)
                ));

        String orderNo = NoGeneratorUtil.generate(userId);

        R<Void> lockResult = productFeignClient.lockStock(StockLockDTO.builder()
                .orderNo(orderNo)
                .spuSkuQuantityMap(spuSkuQuantityMap)
                .expireTime(LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES))
                .build());

        if (!lockResult.isSuccess()) {
            throw new ApiException(lockResult.getMessage());
        }

        order.setOrderNo(orderNo);
        order.setRequestId(requestId);
        order.setUserId(userId);
        order.setTotalAmount(snapshot.getTotalAmount());
        order.setFreightAmount(snapshot.getFreightAmount());
        order.setDiscountAmount(snapshot.getDiscountAmount());
        order.setPayAmount(snapshot.getPayAmount());
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setStockReleaseStatus(StockReleaseStatus.NOT_TRIGGERED.getCode());

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

        orderNo = create(aggregate);
        removeSubmittedCartItems(userId, snapshotItems);

        deleteSnapshot(requestId);

        log.info("订单创建成功, requestId={}, orderNo={}, userId={}", requestId, orderNo, userId);

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
    public Page<OrderAggregate> listByUserId(Long userId, Integer status, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderDao.listByUserId(userId, status);
        if (orders.isEmpty()) {
            return PageUtils.buildPage(orders, Collections.emptyList());
        }

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> orderItems = orderItemDao.selectByOrderIds(orderIds);
        Map<Long, List<OrderItem>> itemsMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<OrderAggregate> aggregates = orders.stream().map(order -> OrderAggregate.builder()
                .order(order)
                .items(itemsMap.get(order.getId()))
                .build()).toList();

        return PageUtils.buildPage(orders, aggregates);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(String orderNo, Long userId, boolean restoreCart) {
        validateAndGetOrder(orderNo, userId);
        cancelByOrderNos(List.of(orderNo), restoreCart);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelByOrderNos(List<String> orderNos) {
        cancelByOrderNos(orderNos, false);
    }

    private void cancelByOrderNos(List<String> orderNos, boolean restoreCart) {
        if (orderNos == null || orderNos.isEmpty()) {
            return;
        }

        orderDao.batchUpdateStatusByOrderNos(
                orderNos,
                OrderStatus.CANCELLED.getCode(),
                StockReleaseStatus.PENDING_RELEASE.getCode()
        );

        eventPublisher.publishEvent(new OrderCancelledEvent(orderNos, restoreCart));

        log.info("批量取消订单完成，orderNos={}", orderNos);
    }


    /**
     * 校验并获取订单
     */
    private Order validateAndGetOrder(String orderNo, Long userId) {
        Order order = orderDao.selectByConditions(userId, orderNo, OrderStatus.PENDING_PAYMENT.getCode());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ApiException("无权操作此订单");
        }
        return order;
    }

    /**
     * 未支付订单取消后，根据订单项回填购物车。
     */
    private void refillCartItems(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return;
        }
        List<Order> orders = orderDao.selectByOrderNos(orderNos);
        if (orders == null || orders.isEmpty()) {
            return;
        }

        Map<String, Order> orderMap = orders.stream()
                .filter(order -> order.getOrderNo() != null)
                .collect(Collectors.toMap(Order::getOrderNo, order -> order, (left, right) -> left));
        if (orderMap.isEmpty()) {
            return;
        }

        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .filter(Objects::nonNull)
                .toList();
        if (orderIds.isEmpty()) {
            return;
        }

        List<OrderItem> orderItems = orderItemDao.selectByOrderIds(orderIds);
        if (orderItems == null || orderItems.isEmpty()) {
            return;
        }

        List<Long> userIds = orders.stream()
                .map(Order::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (userIds.isEmpty()) {
            return;
        }

        Map<String, CartItem> existingCartMap = cartItemDao.selectByUserIds(userIds).stream()
                .collect(Collectors.toMap(
                        item -> buildCartKey(item.getUserId(), item.getSkuId()),
                        item -> item,
                        (left, right) -> left
                ));

        Map<String, CartItem> insertMap = new LinkedHashMap<>();
        Map<Long, Integer> increaseQuantityMap = new LinkedHashMap<>();

        for (OrderItem orderItem : orderItems) {
            Order order = orderMap.get(orderItem.getOrderNo());
            if (order == null || order.getUserId() == null || orderItem.getSkuId() == null) {
                continue;
            }

            String cartKey = buildCartKey(order.getUserId(), orderItem.getSkuId());
            CartItem existing = existingCartMap.get(cartKey);
            if (existing != null && existing.getId() != null) {
                increaseQuantityMap.merge(existing.getId(), orderItem.getQuantity(), Integer::sum);
                continue;
            }

            CartItem pendingInsert = insertMap.get(cartKey);
            if (pendingInsert != null) {
                pendingInsert.setQuantity(pendingInsert.getQuantity() + orderItem.getQuantity());
                continue;
            }

            insertMap.put(cartKey, buildRefillCartItem(order, orderItem));
        }

        if (!insertMap.isEmpty()) {
            cartItemDao.insertBatch(new ArrayList<>(insertMap.values()));
        }

        if (!increaseQuantityMap.isEmpty()) {
            List<CartItem> updateList = increaseQuantityMap.entrySet().stream()
                    .map(entry -> {
                        CartItem cartItem = new CartItem();
                        cartItem.setId(entry.getKey());
                        cartItem.setQuantity(entry.getValue());
                        return cartItem;
                    })
                    .toList();
            cartItemDao.batchIncreaseQuantity(updateList);
        }
    }

    private CartItem buildRefillCartItem(Order order, OrderItem orderItem) {
        CartItem cartItem = new CartItem();
        cartItem.setUserId(order.getUserId());
        cartItem.setSpuId(orderItem.getSpuId());
        cartItem.setSkuId(orderItem.getSkuId());
        cartItem.setQuantity(orderItem.getQuantity());
        cartItem.setChecked(1);
        cartItem.setSpuName(orderItem.getSpuName());
        cartItem.setSkuPic(orderItem.getSkuPic());
        cartItem.setSkuAttrs(orderItem.getSkuAttrs());
        cartItem.setPrice(orderItem.getPrice());
        return cartItem;
    }

    private String buildCartKey(Long userId, Long skuId) {
        return userId + "_" + skuId;
    }

    /**
     * 仅删除当前快照中已下单的购物车项，避免误删其他新勾选商品。
     */
    private void removeSubmittedCartItems(Long userId, List<OrderItemVO> snapshotItems) {
        if (snapshotItems == null || snapshotItems.isEmpty()) {
            return;
        }
        Set<Long> submittedSkuIds = snapshotItems.stream()
                .map(OrderItemVO::getSkuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (submittedSkuIds.isEmpty()) {
            return;
        }
        List<Long> deleteIds = cartItemService.listByUserId(userId).stream()
                .filter(item -> submittedSkuIds.contains(item.getSkuId()))
                .map(CartItem::getId)
                .filter(Objects::nonNull)
                .toList();
        if (!deleteIds.isEmpty()) {
            cartItemService.deleteBatch(deleteIds);
        }
    }

    /**
     * 尝试释放库存
     */
    public Map<String, StockReleaseStatus> tryReleaseStock(List<String> orderNos) {
        Map<String, StockReleaseStatus> resultMap = new HashMap<>();
        try {
            R<List<String>> r = productFeignClient.unlock(orderNos);

            Set<String> failedSet;
            if (r.isSuccess()) {
                failedSet = (r.getData() != null && !r.getData().isEmpty()) ? new HashSet<>(r.getData()) : Collections.emptySet();
            } else {
                failedSet = new HashSet<>(orderNos);
            }

            for (String orderNo : orderNos) {
                if (failedSet.contains(orderNo)) {
                    resultMap.put(orderNo, StockReleaseStatus.RELEASE_FAILED);
                } else {
                    resultMap.put(orderNo, StockReleaseStatus.RELEASED);
                }
            }

            if (!failedSet.isEmpty()) {
                log.warn("部分库存释放失败，failedOrderNos={}，将由定时任务兜底", failedSet);
            }
        } catch (Exception e) {
            log.warn("库存释放异常，orderNos={}，error={}", orderNos, e.getMessage());
            orderNos.forEach(orderNo -> resultMap.put(orderNo, StockReleaseStatus.RELEASE_FAILED));
        }
        return resultMap;
    }

    // ==================== 管理端方法 ====================

    @Override
    public Order findByOrderNo(String orderNo) {
        return orderDao.selectByOrderNo(orderNo);
    }

    @Override
    public List<Order> adminList(OrderQuery query) {
        return orderDao.adminList(query);
    }

    @Override
    public List<OrderItem> listItemsByOrderIds(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyList();
        }
        return orderItemDao.selectByOrderIds(orderIds);
    }

    @Override
    public List<OrderItem> listItemsByOrderNo(String orderNo) {
        return orderItemDao.selectByOrderNo(orderNo);
    }

    @Override
    public int updateOrderSelective(Order order) {
        return orderDao.updateByPrimaryKeySelective(order);
    }

    @Override
    public Map<String, Object> statsTodayOverview(LocalDateTime todayStart) {
        return orderDao.statsTodayOverview(todayStart);
    }

    @Override
    public Long countByStatus(Integer status) {
        return orderDao.countByStatus(status);
    }

    @Override
    public Map<String, Object> statsTotalOverview() {
        return orderDao.statsTotalOverview();
    }

    @Override
    public List<OrderStatsTrendVO> statsTrend(LocalDateTime startDate, LocalDateTime endDate) {
        return orderDao.statsTrend(startDate, endDate);
    }

    @Override
    public List<OrderStatusDistributionVO> statsStatusDistribution() {
        return orderDao.statsStatusDistribution();
    }
}
