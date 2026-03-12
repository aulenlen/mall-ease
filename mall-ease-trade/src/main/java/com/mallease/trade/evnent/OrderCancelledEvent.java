package com.mallease.trade.evnent;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderCancelledEvent {
    List<String> orderNos;
    boolean restoreCart;

    public OrderCancelledEvent(List<String> orderNos, boolean restoreCart) {
        this.orderNos = orderNos;
        this.restoreCart = restoreCart;
    }
}
