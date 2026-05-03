package com.mallease.trade.service.order.handler;

import com.mallease.trade.service.order.handler.bo.OrderContext;
import com.mallease.trade.service.order.handler.enums.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderHandlerInvoker {

    private final List<OrderHandler> handlers;

    public void beforeOrderCreate(OrderContext context) {
        invoke(context, OrderEvent.BEFORE_CREATE, OrderHandler::beforeOrderCreate);
    }

    public void afterOrderPersisted(OrderContext context) {
        invoke(context, OrderEvent.AFTER_PERSISTED, OrderHandler::afterOrderPersisted);
    }

    public void afterOrderCreated(OrderContext context) {
        invoke(context, OrderEvent.AFTER_CREATED, OrderHandler::afterOrderCreated);
    }

    public void afterOrderPaid(OrderContext context) {
        invoke(context, OrderEvent.AFTER_PAID, OrderHandler::afterOrderPaid);
    }

    public void beforeOrderCancel(OrderContext context) {
        invoke(context, OrderEvent.BEFORE_CANCEL, OrderHandler::beforeOrderCancel);
    }

    public void afterOrderCancelled(OrderContext context) {
        invoke(context, OrderEvent.AFTER_CANCELLED, OrderHandler::afterOrderCancelled);
    }

    public void afterOrderExpired(OrderContext context) {
        invoke(context, OrderEvent.AFTER_EXPIRED, OrderHandler::afterOrderExpired);
    }

    public void retryConfirm(OrderContext context) {
        invoke(context, OrderEvent.RETRY_CONFIRM, OrderHandler::retryConfirm);
    }

    public void retryRelease(OrderContext context) {
        invoke(context, OrderEvent.RETRY_RELEASE, OrderHandler::retryRelease);
    }

    private void invoke(OrderContext context, OrderEvent event, BiConsumer<OrderHandler, OrderContext> action) {
        context.setEvent(event);
        matchedHandlers(context).forEach(handler -> action.accept(handler, context));
    }

    private List<OrderHandler> matchedHandlers(OrderContext context) {
        return handlers.stream()
                .filter(handler -> handler.supports(context))
                .sorted(Comparator.comparingInt(OrderHandler::order)
                        .thenComparing(handler -> handler.getClass().getName()))
                .toList();
    }
}
