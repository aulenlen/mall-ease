package com.mallease.trade.evnent;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderCancelledEvent {
    List<String> orderNos;

    public OrderCancelledEvent(List<String> orderNos) {
        this.orderNos = orderNos;
    }
}
