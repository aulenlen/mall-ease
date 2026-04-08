package com.mallease.trade.service.order.handler;

import com.mallease.common.api.R;
import com.mallease.common.enums.OrderSource;
import com.mallease.common.enums.OrderStatus;
import com.mallease.common.enums.OrderStockStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.trade.dal.entity.CartItem;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.dal.mapper.CartItemDao;
import com.mallease.trade.dal.mapper.OrderDao;
import com.mallease.trade.dal.mapper.OrderItemDao;
import com.mallease.trade.feign.product.ProductFeignClient;
import com.mallease.trade.service.order.support.OrderCancelSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class NormalOrderSourceHandler implements OrderSourceHandler {

    private final ProductFeignClient productFeignClient;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final CartItemDao cartItemDao;
    private final OrderCancelSupport orderCancelSupport;
    private final TransactionTemplate transactionTemplate;

    @Override
    public OrderSource source() {
        return OrderSource.NORMAL;
    }

    @Override
    public boolean cancelPending(Order order, boolean restoreCart) {
        releaseOrderStocks(List.of(order), restoreCart, true);
        return true;
    }

    @Override
    public void cancelPending(List<Order> orders, boolean restoreCart) {
        releaseOrderStocks(orders, restoreCart, true);
    }

    @Override
    public void confirmPaid(Order order) {
        try {
            R<Void> result = productFeignClient.confirmStock(order.getOrderNo());
            if (result == null || !result.isSuccess()) {
                markOrderStockConfirmFailed(order.getId());
                throw new ApiException(resolveErrorMessage(result, "确认扣减库存失败"));
            }
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            markOrderStockConfirmFailed(order.getId());
            throw new ApiException("支付成功，订单处理中");
        }
    }

    @Override
    public void closeExpired(List<Order> orders) {
        releaseOrderStocks(orders, false, true);
    }

    @Override
    public void retryConfirm(List<Order> orders) {
        for (Order order : orders) {
            if (order == null || order.getOrderNo() == null || order.getId() == null) {
                continue;
            }
            try {
                markOrderStockConfirming(order);
                R<Void> result = productFeignClient.confirmStock(order.getOrderNo());
                if (result != null && result.isSuccess()) {
                    updateOrderProcessState(order.getId(), OrderStatus.PENDING_SHIPMENT.getCode(), OrderStockStatus.CONFIRMED.getCode());
                } else {
                    markOrderStockConfirmFailed(order.getId());
                }
            } catch (Exception ex) {
                markOrderStockConfirmFailed(order.getId());
                log.warn("重试确认库存失败，orderNo={}", order.getOrderNo(), ex);
            }
        }
    }

    @Override
    public void retryRelease(List<Order> orders, boolean restoreCart, boolean needTransition) {
        releaseOrderStocks(orders, restoreCart, needTransition);
    }

    private void releaseOrderStocks(List<Order> orders, boolean restoreCart, boolean needTransition) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        List<String> processingOrderNos = needTransition
                ? transitionOrdersToReleasing(orders)
                : orders.stream()
                .map(Order::getOrderNo)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (processingOrderNos.isEmpty()) {
            return;
        }

        Set<String> failedOrderNos = new HashSet<>();
        try {
            R<List<String>> result = productFeignClient.unlock(processingOrderNos);
            if (result == null || !result.isSuccess()) {
                failedOrderNos.addAll(processingOrderNos);
            } else if (result.getData() != null) {
                failedOrderNos.addAll(result.getData());
            }
        } catch (Exception ex) {
            log.warn("调用 Product 释放库存失败，orderNos={}", processingOrderNos, ex);
            failedOrderNos.addAll(processingOrderNos);
        }

        List<String> releasedOrderNos = processingOrderNos.stream()
                .filter(orderNo -> !failedOrderNos.contains(orderNo))
                .toList();

        if (!releasedOrderNos.isEmpty()) {
            orderCancelSupport.markCancelled(
                    releasedOrderNos,
                    OrderStockStatus.RELEASED.getCode(),
                    () -> {
                        if (restoreCart) {
                            refillCartItems(releasedOrderNos);
                        }
                    }
            );
        }

        if (!failedOrderNos.isEmpty()) {
            orderDao.updateStockProcessStatusByOrderNos(
                    new ArrayList<>(failedOrderNos),
                    OrderStockStatus.RELEASE_FAILED.getCode()
            );
        }
    }

    private List<String> transitionOrdersToReleasing(List<Order> orders) {
        List<String> orderNos = orders.stream()
                .filter(Objects::nonNull)
                .map(Order::getOrderNo)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (orderNos.isEmpty()) {
            return Collections.emptyList();
        }

        int updatedRows = orderDao.batchUpdateStatusAndStockProcessByOrderNos(
                orderNos,
                OrderStatus.PENDING_PAYMENT.getCode(),
                OrderStatus.CANCELLED.getCode(),
                OrderStockStatus.RELEASING.getCode()
        );
        if (updatedRows <= 0) {
            return Collections.emptyList();
        }
        if (updatedRows == orderNos.size()) {
            return orderNos;
        }

        return orderDao.selectByOrderNos(orderNos).stream()
                .filter(Objects::nonNull)
                .filter(order -> Objects.equals(order.getStatus(), OrderStatus.CANCELLED.getCode()))
                .filter(order -> Objects.equals(order.getStockProcessStatus(), OrderStockStatus.RELEASING.getCode()))
                .map(Order::getOrderNo)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
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

    private String buildCartKey(Long userId, Long skuId) {
        return userId + "_" + skuId;
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

    private void markOrderStockConfirmFailed(Long orderId) {
        transactionTemplate.executeWithoutResult(status ->
                orderDao.updateByPrimaryKeySelective(Order.builder()
                        .id(orderId)
                        .status(OrderStatus.PAID.getCode())
                        .stockProcessStatus(OrderStockStatus.CONFIRM_FAILED.getCode())
                        .build())
        );
    }

    private String resolveErrorMessage(R<?> result, String defaultMessage) {
        if (result == null || result.getMessage() == null || result.getMessage().isBlank()) {
            return defaultMessage;
        }
        return result.getMessage();
    }

    private void markOrderStockConfirming(Order order) {
        if (Objects.equals(order.getStatus(), OrderStatus.PAID.getCode())
                && Objects.equals(order.getStockProcessStatus(), OrderStockStatus.CONFIRMING.getCode())) {
            return;
        }
        transactionTemplate.executeWithoutResult(status ->
                orderDao.updateByPrimaryKeySelective(Order.builder()
                        .id(order.getId())
                        .status(OrderStatus.PAID.getCode())
                        .stockProcessStatus(OrderStockStatus.CONFIRMING.getCode())
                        .build())
        );
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
}
