package com.mallease.trade.service.order.handler;

import com.mallease.trade.service.order.handler.bo.OrderContext;

public interface OrderHandler {

    boolean supports(OrderContext context);

    default int order() {
        return 0;
    }

    default void beforeOrderCreate(OrderContext context) {
    }

    default void afterOrderPersisted(OrderContext context) {
    }

    default void afterOrderCreated(OrderContext context) {
    }

    default void afterOrderPaid(OrderContext context) {
    }

    default void beforeOrderCancel(OrderContext context) {
    }

    default void afterOrderCancelled(OrderContext context) {
    }

    default void afterOrderExpired(OrderContext context) {
    }

    default void retryConfirm(OrderContext context) {
    }

    default void retryRelease(OrderContext context) {
    }
}
