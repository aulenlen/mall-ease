package com.mallease.trade.service.order.handler;

import com.mallease.common.enums.OrderSource;
import com.mallease.trade.dal.entity.Order;

import java.util.List;

public interface OrderSourceHandler {
    OrderSource source();

    boolean cancelPending(Order order, boolean restoreCart);

    default void cancelPending(List<Order> orders, boolean restoreCart) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        for (Order order : orders) {
            if (order == null) {
                continue;
            }
            cancelPending(order, restoreCart);
        }
    }

    void confirmPaid(Order order);

    void closeExpired(List<Order> orders);

    void retryConfirm(List<Order> orders);

    void retryRelease(List<Order> orders, boolean restoreCart, boolean needTransition);
}
