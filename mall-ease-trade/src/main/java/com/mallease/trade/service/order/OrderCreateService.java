package com.mallease.trade.service.order;

import com.mallease.common.dto.remote.FlashCreateOrderReqDTO;
import com.mallease.common.dto.remote.FlashCreateOrderRespDTO;
import com.mallease.common.enums.OrderSource;
import com.mallease.common.enums.OrderStatus;
import com.mallease.common.enums.OrderStockStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.common.util.NoGeneratorUtil;
import com.mallease.trade.constant.OrderCacheKeys;
import com.mallease.trade.controller.portal.order.vo.OrderConfirmRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderItemRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitReqVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitRespVO;
import com.mallease.trade.convert.order.OrderConvert;
import com.mallease.trade.dal.entity.CartItem;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.dal.mapper.OrderDao;
import com.mallease.trade.dal.mapper.OrderItemDao;
import com.mallease.trade.service.cart.CartService;
import com.mallease.trade.service.order.handler.OrderHandlerInvoker;
import com.mallease.trade.service.order.handler.bo.OrderContext;
import com.mallease.trade.service.order.handler.exception.StockLockFailedException;
import com.mallease.trade.service.order.handler.exception.StockLockUnknownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.mallease.trade.constant.OrderConstant.PAYMENT_TIMEOUT_MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCreateService {

    private final TransactionTemplate transactionTemplate;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final CartService cartItemService;
    private final TypedRedisService typedRedisService;
    private final OrderConvert orderConvert;
    private final OrderHandlerInvoker orderHandlerInvoker;

    public OrderConfirmRespVO createOrderSnapshot(Long userId) {
        if (userId == null) {
            throw new ApiException("用户未登录");
        }
        List<CartItem> userItems = cartItemService.listCheckedByUserId(userId);
        if (userItems.isEmpty()) {
            throw new ApiException("请选择要结算的商品");
        }

        String requestId = UUID.randomUUID().toString().replace("-", "");
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemRespVO> items = new ArrayList<>();

        for (CartItem cart : userItems) {
            BigDecimal subtotal = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            items.add(OrderItemRespVO.builder()
                    .spuId(cart.getSpuId())
                    .skuId(cart.getSkuId())
                    .spuName(cart.getSpuName())
                    .skuPic(cart.getSkuPic())
                    .skuAttrs(cart.getSkuAttrs())
                    .price(cart.getPrice())
                    .quantity(cart.getQuantity())
                    .subtotal(subtotal)
                    .build());
        }

        OrderConfirmRespVO snapshot = OrderConfirmRespVO.builder()
                .requestId(requestId)
                .userId(userId)
                .items(items)
                .totalAmount(totalAmount)
                .freightAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .payAmount(totalAmount)
                .createTime(LocalDateTime.now())
                .build();
        saveOrderSnapshot(userId, requestId, snapshot);
        return snapshot;
    }

    public OrderSubmitRespVO submitNormalOrder(Long userId, OrderSubmitReqVO reqVO) {
        OrderConfirmRespVO snapshot = loadAndValidateOrderSnapshot(userId, reqVO.getRequestId());
        OrderContext context = buildNormalCreateContext(userId, reqVO, snapshot);
        Order order = createOrderByContext(context);

        if (!context.isOrderPersisted()) {
            log.info("订单已存在，直接返回已有订单: requestId={}, orderNo={}, status={}", context.getRequestId(), order.getOrderNo(), order.getStatus());
            return buildSubmitOrderResponse(order);
        }

        log.info("订单创建成功，requestId={}, orderNo={}, userId={}", context.getRequestId(), order.getOrderNo(), userId);
        return buildSubmitOrderResponse(order);
    }

    public FlashCreateOrderRespDTO submitFlashOrder(FlashCreateOrderReqDTO req) {
        validateFlashCreateOrderRequest(req);
        OrderContext context = buildFlashCreateContext(req);
        Order order = createOrderByContext(context);
        return buildFlashCreateOrderResponse(order);
    }

    public Order createOrderByContext(OrderContext context) {
        Order existOrder = findReusableOrderByRequestId(context.getRequestId());
        if (existOrder != null) {
            context.setOrder(existOrder);
            return existOrder;
        }

        orderHandlerInvoker.beforeOrderCreate(context);

        Order order;
        try {
            order = persistOrderAndItems(context.getOrder(), context.getOrderItems());
        } catch (DuplicateKeyException ex) {
            Order duplicatedOrder = findReusableOrderByRequestId(context.getRequestId());
            if (duplicatedOrder == null) {
                log.error("订单创建发生唯一键冲突但未查询到幂等订单，requestId={}", context.getRequestId(), ex);
                throw new ApiException("订单创建失败");
            }
            log.info("订单并发重复提交，返回已有订单: requestId={}, orderNo={}, status={}",
                    context.getRequestId(), duplicatedOrder.getOrderNo(), duplicatedOrder.getStatus());
            context.setOrder(duplicatedOrder);
            return duplicatedOrder;
        }

        context.setOrder(order);
        context.setOrderPersisted(true);

        try {
            orderHandlerInvoker.afterOrderPersisted(context);
        } catch (StockLockUnknownException ex) {
            updateOrderProcessState(order.getId(), OrderStatus.PROCESSING.getCode(), OrderStockStatus.LOCK_UNKNOWN.getCode());
            log.warn("调用 Product 锁库超时或异常，orderNo={}, requestId={}", order.getOrderNo(), context.getRequestId(), ex);
            throw new ApiException("订单处理中，请稍后在订单列表查看");
        } catch (StockLockFailedException ex) {
            updateOrderProcessState(order.getId(), OrderStatus.FAILED.getCode(), OrderStockStatus.LOCK_FAILED.getCode());
            throw new ApiException(ex.getMessage());
        }

        Integer createdStockStatus = resolvePostCreateStockStatus(context);
        updateOrderProcessState(order.getId(), OrderStatus.PENDING_PAYMENT.getCode(), createdStockStatus);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setStockProcessStatus(createdStockStatus);

        orderHandlerInvoker.afterOrderCreated(context);
        return order;
    }

    private void validateFlashCreateOrderRequest(FlashCreateOrderReqDTO req) {
        if (req == null) {
            throw new ApiException("秒杀订单参数不能为空");
        }
        if (req.getUserId() == null) {
            throw new ApiException("用户信息不能为空");
        }
        if (req.getRequestId() == null || req.getRequestId().isBlank()) {
            throw new ApiException("请求ID不能为空");
        }
        if (req.getSessionId() == null) {
            throw new ApiException("秒杀场次不能为空");
        }
        if (req.getFlashProductId() == null) {
            throw new ApiException("秒杀商品不能为空");
        }
        if (req.getSpuId() == null || req.getSkuId() == null) {
            throw new ApiException("商品信息不能为空");
        }
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new ApiException("商品数量不正确");
        }
        if (req.getFlashPrice() == null || req.getFlashPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("秒杀价格不正确");
        }
        if (req.getSpuName() == null || req.getSpuName().isBlank()) {
            throw new ApiException("商品名称不能为空");
        }
        if (req.getReceiverName() == null || req.getReceiverName().isBlank()) {
            throw new ApiException("收货人姓名不能为空");
        }
        if (req.getReceiverPhone() == null || req.getReceiverPhone().isBlank()) {
            throw new ApiException("收货人电话不能为空");
        }
        if (req.getReceiverAddress() == null || req.getReceiverAddress().isBlank()) {
            throw new ApiException("详细地址不能为空");
        }
    }

    private Order buildFlashOrder(FlashCreateOrderReqDTO req, String orderNo,
                                  LocalDateTime payExpireTime, BigDecimal payAmount) {
        return Order.builder()
                .orderNo(orderNo)
                .requestId(req.getRequestId())
                .userId(req.getUserId())
                .orderSource(OrderSource.FLASH.getCode())
                .flashSessionId(req.getSessionId())
                .flashProductId(req.getFlashProductId())
                .receiverName(req.getReceiverName())
                .receiverPhone(req.getReceiverPhone())
                .receiverProvince(req.getReceiverProvince())
                .receiverCity(req.getReceiverCity())
                .receiverDistrict(req.getReceiverDistrict())
                .receiverAddress(req.getReceiverAddress())
                .totalAmount(payAmount)
                .freightAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .payAmount(payAmount)
                .payExpireTime(payExpireTime)
                .status(OrderStatus.PENDING_PAYMENT.getCode())
                .remark(req.getRemark())
                .stockProcessStatus(OrderStockStatus.INIT.getCode())
                .build();
    }

    private OrderItem buildFlashOrderItem(FlashCreateOrderReqDTO req, String orderNo, BigDecimal payAmount) {
        return OrderItem.builder()
                .orderNo(orderNo)
                .spuId(req.getSpuId())
                .skuId(req.getSkuId())
                .spuName(req.getSpuName())
                .skuPic(req.getSkuPic())
                .skuAttrs(req.getSkuAttrs())
                .price(req.getFlashPrice())
                .quantity(req.getQuantity())
                .subtotal(payAmount)
                .build();
    }

    private OrderContext buildFlashCreateContext(FlashCreateOrderReqDTO req) {
        String orderNo = NoGeneratorUtil.generate(req.getUserId());
        LocalDateTime payExpireTime = LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
        BigDecimal payAmount = req.getFlashPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        Order order = buildFlashOrder(req, orderNo, payExpireTime, payAmount);
        OrderItem orderItem = buildFlashOrderItem(req, orderNo, payAmount);

        OrderContext context = new OrderContext();
        context.setUserId(req.getUserId());
        context.setRequestId(req.getRequestId());
        context.setOrderSource(OrderSource.FLASH);
        context.setOrderNo(orderNo);
        context.setPayExpireTime(payExpireTime);
        context.setRemark(req.getRemark());
        context.setTotalAmount(payAmount);
        context.setFreightAmount(BigDecimal.ZERO);
        context.setDiscountAmount(BigDecimal.ZERO);
        context.setPayAmount(payAmount);
        context.setFlashSessionId(req.getSessionId());
        context.setFlashProductId(req.getFlashProductId());
        context.setSourceSnapshot(req);
        context.setOrder(order);
        context.setOrderItems(List.of(orderItem));
        return context;
    }

    private FlashCreateOrderRespDTO buildFlashCreateOrderResponse(Order order) {
        return FlashCreateOrderRespDTO.builder()
                .orderNo(order.getOrderNo())
                .payAmount(order.getPayAmount())
                .payExpireTime(resolvePayExpireTime(order))
                .orderStatus(order.getStatus())
                .serverTime(LocalDateTime.now())
                .build();
    }

    private OrderConfirmRespVO loadAndValidateOrderSnapshot(Long userId, String requestId) {
        OrderConfirmRespVO snapshot = loadOrderSnapshot(userId, requestId);
        if (snapshot == null) {
            throw new ApiException("订单已过期，请重新结算");
        }
        if (!Objects.equals(snapshot.getUserId(), userId)) {
            throw new ApiException("非法请求");
        }
        return snapshot;
    }

    private Order findReusableOrderByRequestId(String requestId) {
        Order existOrder = orderDao.selectByRequestId(requestId);
        if (existOrder == null) {
            return null;
        }
        if (OrderStatus.FAILED.getCode() == existOrder.getStatus()
                || OrderStatus.CANCELLED.getCode() == existOrder.getStatus()) {
            throw new ApiException("订单已失效，请重新结算");
        }
        return existOrder;
    }

    private void saveOrderSnapshot(Long userId, String requestId, OrderConfirmRespVO snapshot) {
        if (userId == null || requestId == null || requestId.isBlank() || snapshot == null) {
            return;
        }
        typedRedisService.setJson(OrderCacheKeys.snapshotKey(userId, requestId), snapshot, OrderCacheKeys.snapshotTtlSeconds());
    }

    private OrderConfirmRespVO loadOrderSnapshot(Long userId, String requestId) {
        if (userId == null || requestId == null || requestId.isBlank()) {
            return null;
        }
        return typedRedisService.getJson(OrderCacheKeys.snapshotKey(userId, requestId), OrderConfirmRespVO.class);
    }

    private OrderContext buildNormalCreateContext(Long userId, OrderSubmitReqVO reqVO, OrderConfirmRespVO snapshot) {
        List<OrderItemRespVO> snapshotItems = snapshot.getItems();
        if (snapshotItems == null || snapshotItems.isEmpty()) {
            throw new ApiException("未选择任何商品");
        }

        String requestId = reqVO.getRequestId();
        String orderNo = NoGeneratorUtil.generate(userId);
        LocalDateTime payExpireTime = LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
        Order order = buildNormalOrderEntity(userId, reqVO, snapshot, requestId, orderNo, payExpireTime);
        List<OrderItem> orderItems = buildOrderItemEntities(orderNo, snapshotItems);

        OrderContext context = new OrderContext();
        context.setUserId(userId);
        context.setRequestId(requestId);
        context.setOrderSource(OrderSource.NORMAL);
        context.setOrderNo(orderNo);
        context.setPayExpireTime(payExpireTime);
        context.setReceiver(reqVO);
        context.setRemark(reqVO.getRemark());
        context.setTotalAmount(snapshot.getTotalAmount());
        context.setFreightAmount(snapshot.getFreightAmount());
        context.setDiscountAmount(snapshot.getDiscountAmount());
        context.setPayAmount(snapshot.getPayAmount());
        context.setSourceSnapshot(snapshot);
        context.setOrder(order);
        context.setOrderItems(orderItems);
        return context;
    }

    private Order buildNormalOrderEntity(Long userId, OrderSubmitReqVO reqVO, OrderConfirmRespVO snapshot,
                                         String requestId, String orderNo, LocalDateTime payExpireTime) {
        Order order = orderConvert.toOrder(reqVO);
        order.setOrderNo(orderNo);
        order.setRequestId(requestId);
        order.setUserId(userId);
        order.setOrderSource(OrderSource.NORMAL.getCode());
        order.setTotalAmount(snapshot.getTotalAmount());
        order.setFreightAmount(snapshot.getFreightAmount());
        order.setDiscountAmount(snapshot.getDiscountAmount());
        order.setPayAmount(snapshot.getPayAmount());
        order.setPayExpireTime(payExpireTime);
        order.setStatus(OrderStatus.PROCESSING.getCode());
        order.setStockProcessStatus(OrderStockStatus.LOCKING.getCode());
        return order;
    }

    private List<OrderItem> buildOrderItemEntities(String orderNo, List<OrderItemRespVO> snapshotItems) {
        return snapshotItems.stream()
                .map(item -> OrderItem.builder()
                        .orderNo(orderNo)
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
    }

    private Order persistOrderAndItems(Order order, List<OrderItem> orderItems) {
        Boolean created = transactionTemplate.execute(status -> {
            try {
                orderDao.insert(order);
                for (OrderItem orderItem : orderItems) {
                    orderItem.setOrderId(order.getId());
                }
                orderItemDao.insertBatch(orderItems);
                return Boolean.TRUE;
            } catch (DuplicateKeyException e) {
                status.setRollbackOnly();
                throw e;
            } catch (Exception ex) {
                status.setRollbackOnly();
                log.error("创建占位订单失败，orderNo={}", order.getOrderNo(), ex);
                return Boolean.FALSE;
            }
        });

        if (!Boolean.TRUE.equals(created)) {
            throw new ApiException("订单创建失败");
        }
        return order;
    }

    private Integer resolvePostCreateStockStatus(OrderContext context) {
        if (context.getOrderSource() == OrderSource.NORMAL) {
            return OrderStockStatus.LOCKED.getCode();
        }
        if (context.getOrderSource() == OrderSource.FLASH) {
            return OrderStockStatus.INIT.getCode();
        }
        return OrderStockStatus.INIT.getCode();
    }

    private void updateOrderProcessState(Long orderId, Integer orderStatus, Integer stockProcessStatus) {
        transactionTemplate.executeWithoutResult(status ->
                orderDao.updateByPrimaryKeySelective(Order.builder()
                        .id(orderId)
                        .status(orderStatus)
                        .stockProcessStatus(stockProcessStatus)
                        .build())
        );
    }

    private OrderSubmitRespVO buildSubmitOrderResponse(Order order) {
        return OrderSubmitRespVO.builder()
                .orderNo(order.getOrderNo())
                .payAmount(order.getPayAmount())
                .payExpireTime(resolvePayExpireTime(order))
                .orderStatus(order.getStatus())
                .serverTime(LocalDateTime.now())
                .build();
    }

    private LocalDateTime resolvePayExpireTime(Order order) {
        if (order.getPayExpireTime() != null) {
            return order.getPayExpireTime();
        }
        if (order.getCreateTime() != null) {
            return order.getCreateTime().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
        }
        return LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES);
    }
}
