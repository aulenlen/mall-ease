package com.mallease.product.event;

import com.mallease.product.model.data.entity.StockReservation;
import lombok.Getter;

import java.util.List;

/**
 * 库存锁定事件（用于事务提交后更新 Redis 缓存）
 *
 * @author: Aulen
 * @create: 2026-02-01
 */
@Getter
public class StockLockEvent {

    private final List<StockReservation> reservations;

    public StockLockEvent(List<StockReservation> reservations) {
        this.reservations = reservations;
    }
}
