package com.mallease.trade.service.order;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.StockReservationStatusDTO;
import com.mallease.common.enums.OrderSource;
import com.mallease.common.enums.OrderStatus;
import com.mallease.common.enums.OrderStockStatus;
import com.mallease.common.enums.PaymentStatus;
import com.mallease.common.enums.ReservationAggregateStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.dal.entity.CartItem;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.dal.mapper.CartItemDao;
import com.mallease.trade.dal.mapper.OrderDao;
import com.mallease.trade.dal.mapper.OrderItemDao;
import com.mallease.trade.dal.mapper.PaymentOrderDao;
import com.mallease.trade.feign.product.ProductFeignClient;
import com.mallease.trade.service.order.handler.OrderHandlerInvoker;
import com.mallease.trade.service.order.handler.bo.OrderContext;
import com.mallease.trade.service.order.handler.enums.OrderLifecycleAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mallease.trade.constant.OrderConstant.PAYMENT_TIMEOUT_MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderLifecycleService {

    private final TransactionTemplate transactionTemplate;
    private final CartItemDao cartItemDao;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final PaymentOrderDao paymentOrderDao;
    private final ProductFeignClient productFeignClient;
    private final OrderHandlerInvoker orderHandlerInvoker;

    public boolean cancelOrder(String orderNo, Long userId, boolean restoreCart) {
        Order order = validatePendingOrderAccess(orderNo, userId);
        cancelPendingOrder(order, restoreCart, OrderLifecycleAction.CANCEL);
        return true;
    }

    public void cancelOrders(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return;
        }
        List<Order> orders = orderDao.selectByOrderNos(orderNos);
        if (orders == null || orders.isEmpty()) {
            return;
        }

        List<Order> pendingOrders = orders.stream()
                .filter(order -> Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode()))
                .toList();
        List<Order> directCancelOrders = orders.stream()
                .filter(order -> !Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode()))
                .toList();

        if (!directCancelOrders.isEmpty()) {
            markOrdersCancelled(directCancelOrders);
        }
        for (Order order : pendingOrders) {
            cancelPendingOrder(order, false, OrderLifecycleAction.CANCEL);
        }
    }

    public void advanceOrderToPaid(String orderNo) {
        Order order = orderDao.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())
                && Objects.equals(order.getStockProcessStatus(), OrderStockStatus.CONFIRMING.getCode())) {
            return;
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode())
                && !Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())) {
            throw new ApiException("当前订单状态不允许推进支付");
        }
        orderDao.updateByPrimaryKeySelective(Order.builder()
                .id(order.getId())
                .status(OrderStatus.PAID.getCode())
                .stockProcessStatus(OrderStockStatus.CONFIRMING.getCode())
                .build());
    }

    public void confirmPaidOrder(String orderNo) {
        Order order = orderDao.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (Objects.equals(order.getStatus(), OrderStatus.PENDING_SHIPMENT.getCode())
                && Objects.equals(order.getStockProcessStatus(), OrderStockStatus.CONFIRMED.getCode())) {
            return;
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING_PAYMENT.getCode())
                && !Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())) {
            throw new ApiException("当前订单状态不允许确认库存");
        }

        confirmOrderStock(order, false);
    }

    public void recoverLockingOrders(int limit) {
        List<Order> orders = orderDao.listByStatusAndStockProcessStatuses(
                OrderStatus.PROCESSING.getCode(),
                List.of(OrderStockStatus.LOCKING.getCode(), OrderStockStatus.LOCK_UNKNOWN.getCode()),
                limit
        );
        if (orders == null || orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {
            if (order == null || order.getOrderNo() == null || order.getId() == null) {
                continue;
            }
            try {
                R<StockReservationStatusDTO> result = productFeignClient.queryReservationStatus(order.getOrderNo());
                if (result == null || !result.isSuccess() || result.getData() == null || result.getData().getStatus() == null) {
                    continue;
                }
                reconcileLockingOrder(order, result.getData());
            } catch (Exception ex) {
                log.warn("回查锁库结果失败，orderNo={}", order.getOrderNo(), ex);
            }
        }
    }

    public void closeExpiredPendingOrders(int limit) {
        List<Order> orders = orderDao.listExpiredPendingPayment(
                OrderStatus.PENDING_PAYMENT.getCode(),
                LocalDateTime.now(),
                limit
        );
        if (orders == null || orders.isEmpty()) {
            return;
        }
        for (Order order : orders) {
            cancelPendingOrder(order, false, OrderLifecycleAction.EXPIRE);
        }
    }

    public void retryPayConfirmingOrders(int limit) {
        List<Order> orders = orderDao.listByStatusAndStockProcessStatuses(
                OrderStatus.PAID.getCode(),
                List.of(OrderStockStatus.CONFIRMING.getCode(), OrderStockStatus.CONFIRM_FAILED.getCode()),
                limit
        );
        if (orders == null || orders.isEmpty()) {
            return;
        }

        for (Order order : orders) {
            confirmOrderStock(order, true);
        }
    }

    public void retryReleasingOrders(int limit) {
        List<Order> orders = orderDao.listByStatusAndStockProcessStatuses(
                OrderStatus.CANCELLED.getCode(),
                List.of(OrderStockStatus.RELEASING.getCode(), OrderStockStatus.RELEASE_FAILED.getCode()),
                limit
        );
        if (orders == null || orders.isEmpty()) {
            return;
        }
        for (Order order : orders) {
            retryReleaseOrder(order);
        }
    }

    private void confirmOrderStock(Order order, boolean retry) {
        if (order == null || order.getId() == null) {
            return;
        }

        markOrderStockConfirming(order);

        try {
            OrderContext context = buildOrderContextFromOrder(order);
            if (retry) {
                orderHandlerInvoker.retryConfirm(context);
            } else {
                orderHandlerInvoker.afterOrderPaid(context);
            }
            updateOrderProcessState(order.getId(), OrderStatus.PENDING_SHIPMENT.getCode(), OrderStockStatus.CONFIRMED.getCode());
        } catch (ApiException ex) {
            markOrderStockConfirmFailed(order.getId());
            if (!retry) {
                throw ex;
            }
            log.warn("重试确认库存失败，orderNo={}", order.getOrderNo(), ex);
        } catch (Exception ex) {
            markOrderStockConfirmFailed(order.getId());
            if (!retry) {
                throw new ApiException("支付成功，订单处理中");
            }
            log.warn("重试确认库存失败，orderNo={}", order.getOrderNo(), ex);
        }
    }

    private void cancelPendingOrder(Order order, boolean restoreCart, OrderLifecycleAction action) {
        if (order == null || order.getOrderNo() == null) {
            return;
        }
        if (!transitionOrderToReleasing(order)) {
            return;
        }

        try {
            OrderContext context = buildOrderContextFromOrder(order);
            if (action == OrderLifecycleAction.EXPIRE) {
                orderHandlerInvoker.afterOrderExpired(context);
            } else {
                orderHandlerInvoker.afterOrderCancelled(context);
            }
            markOrderReleaseSucceeded(order, restoreCart);
        } catch (Exception ex) {
            markOrderReleaseFailed(order);
            log.warn("订单资源释放失败，orderNo={}, action={}", order.getOrderNo(), action, ex);
        }
    }

    private void retryReleaseOrder(Order order) {
        if (order == null || order.getOrderNo() == null) {
            return;
        }
        try {
            OrderContext context = buildOrderContextFromOrder(order);
            orderHandlerInvoker.retryRelease(context);
            markOrderReleaseSucceeded(order, false);
        } catch (Exception ex) {
            markOrderReleaseFailed(order);
            log.warn("重试释放订单资源失败，orderNo={}", order.getOrderNo(), ex);
        }
    }

    private boolean transitionOrderToReleasing(Order order) {
        int updatedRows = orderDao.batchUpdateStatusAndStockProcessByOrderNos(
                List.of(order.getOrderNo()),
                OrderStatus.PENDING_PAYMENT.getCode(),
                OrderStatus.CANCELLED.getCode(),
                OrderStockStatus.RELEASING.getCode()
        );
        if (updatedRows <= 0) {
            return false;
        }
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setStockProcessStatus(OrderStockStatus.RELEASING.getCode());
        return true;
    }

    private void markOrderReleaseSucceeded(Order order, boolean restoreCart) {
        if (order == null || order.getOrderNo() == null) {
            return;
        }
        List<String> orderNos = List.of(order.getOrderNo());
        transactionTemplate.executeWithoutResult(status -> {
            orderDao.batchUpdateStatusByOrderNos(
                    orderNos,
                    OrderStatus.CANCELLED.getCode(),
                    OrderStockStatus.RELEASED.getCode()
            );
            paymentOrderDao.closeByOrderNos(orderNos, PaymentStatus.CLOSED.getCode());
            if (restoreCart) {
                refillCartItems(orderNos);
            }
        });
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setStockProcessStatus(OrderStockStatus.RELEASED.getCode());
    }

    private void markOrderReleaseFailed(Order order) {
        if (order == null || order.getOrderNo() == null) {
            return;
        }
        List<String> orderNos = List.of(order.getOrderNo());
        transactionTemplate.executeWithoutResult(status -> {
            orderDao.updateStockProcessStatusByOrderNos(orderNos, OrderStockStatus.RELEASE_FAILED.getCode());
            paymentOrderDao.closeByOrderNos(orderNos, PaymentStatus.CLOSED.getCode());
        });
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setStockProcessStatus(OrderStockStatus.RELEASE_FAILED.getCode());
    }

    private OrderContext buildOrderContextFromOrder(Order order) {
        OrderContext context = new OrderContext();
        context.setUserId(order.getUserId());
        context.setRequestId(order.getRequestId());
        context.setOrderSource(resolveOrderSource(order));
        context.setOrderNo(order.getOrderNo());
        context.setPayExpireTime(resolvePayExpireTime(order));
        context.setRemark(order.getRemark());
        context.setTotalAmount(order.getTotalAmount());
        context.setFreightAmount(order.getFreightAmount());
        context.setDiscountAmount(order.getDiscountAmount());
        context.setPayAmount(order.getPayAmount());
        context.setFlashSessionId(order.getFlashSessionId());
        context.setFlashProductId(order.getFlashProductId());
        context.setOrder(order);
        if (order.getId() != null) {
            context.setOrderItems(orderItemDao.selectByOrderId(order.getId()));
        }
        return context;
    }

    private OrderSource resolveOrderSource(Order order) {
        if (order == null) {
            return OrderSource.NORMAL;
        }
        OrderSource orderSource = OrderSource.fromCode(order.getOrderSource());
        if (orderSource == null) {
            throw new ApiException("不支持的订单来源: " + order.getOrderSource());
        }
        return orderSource;
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

    private void reconcileLockingOrder(Order order, StockReservationStatusDTO reservationStatus) {
        ReservationAggregateStatus aggregateStatus = reservationStatus.getStatus();
        if (aggregateStatus == null) {
            return;
        }
        switch (aggregateStatus) {
            case ALL_LOCKED ->
                    updateOrderProcessState(order.getId(), OrderStatus.PENDING_PAYMENT.getCode(), OrderStockStatus.LOCKED.getCode());
            case ALL_CONFIRMED ->
                    updateOrderProcessState(order.getId(), OrderStatus.PENDING_SHIPMENT.getCode(), OrderStockStatus.CONFIRMED.getCode());
            case ALL_RELEASED, NOT_FOUND ->
                    updateOrderProcessState(order.getId(), OrderStatus.FAILED.getCode(), OrderStockStatus.LOCK_FAILED.getCode());
            case INCONSISTENT -> log.error(
                    "库存预占状态不一致，orderNo={}, totalCount={}, lockedCount={}, confirmedCount={}, releasedCount={}",
                    order.getOrderNo(),
                    reservationStatus.getTotalCount(),
                    reservationStatus.getLockedCount(),
                    reservationStatus.getConfirmedCount(),
                    reservationStatus.getReleasedCount()
            );
        }
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

    private void markOrderStockConfirming(Order order) {
        if (Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())
                && Objects.equals(order.getStockProcessStatus(), OrderStockStatus.CONFIRMING.getCode())) {
            return;
        }
        orderDao.updateByPrimaryKeySelective(Order.builder()
                .id(order.getId())
                .status(OrderStatus.PAID.getCode())
                .stockProcessStatus(OrderStockStatus.CONFIRMING.getCode())
                .build());
    }

    private void markOrderStockConfirmFailed(Long orderId) {
        if (orderId == null) {
            return;
        }
        transactionTemplate.executeWithoutResult(status ->
                orderDao.updateByPrimaryKeySelective(Order.builder()
                        .id(orderId)
                        .status(OrderStatus.PAID.getCode())
                        .stockProcessStatus(OrderStockStatus.CONFIRM_FAILED.getCode())
                        .build())
        );
    }

    private void markOrdersCancelled(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        transactionTemplate.executeWithoutResult(status -> {
            for (Order order : orders) {
                if (order == null || order.getId() == null) {
                    continue;
                }
                orderDao.updateByPrimaryKeySelective(Order.builder()
                        .id(order.getId())
                        .status(OrderStatus.CANCELLED.getCode())
                        .build());
            }
        });
    }

    private Order validatePendingOrderAccess(String orderNo, Long userId) {
        Order order = orderDao.selectByConditions(userId, orderNo, OrderStatus.PENDING_PAYMENT.getCode());
        if (order == null) {
            throw new ApiException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ApiException("无权操作此订单");
        }
        return order;
    }

    private void refillCartItems(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return;
        }
        List<Order> orders = orderDao.selectByOrderNos(orderNos);
        if (orders == null || orders.isEmpty()) {
            return;
        }

        Map<String, Order> orderMap = orders.stream()
                .filter(item -> item.getOrderNo() != null)
                .collect(Collectors.toMap(Order::getOrderNo, item -> item, (left, right) -> left));
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
}
