package com.mallease.product.event;

import com.mallease.product.model.data.entity.StockReservation;
import lombok.Getter;

import java.util.List;

@Getter
public class StockReleaseEvent {
    private final List<StockReservation> reservations;

    public StockReleaseEvent(List<StockReservation> reservations) {
        this.reservations = reservations;
    }
}
