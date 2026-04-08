package com.mallease.trade.service.order.handler;

import com.mallease.common.enums.OrderSource;
import com.mallease.common.exception.ApiException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderSourceHandlerDispatcher {
    private final Map<OrderSource, OrderSourceHandler> handlerMap;

    public OrderSourceHandlerDispatcher(List<OrderSourceHandler> handlers) {
        this.handlerMap = handlers.stream().collect(Collectors.toMap(OrderSourceHandler::source, h -> h));
    }

    public OrderSourceHandler getHandler(OrderSource source) {
        OrderSourceHandler handler = handlerMap.get(source);
        if (handler == null) {
            throw new ApiException("不支持的订单来源: " + source);
        }
        return handler;
    }
}
