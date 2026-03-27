package com.mallease.common.enums;

/**
 * Product 侧订单级库存预占聚合状态。
 */
public enum ReservationAggregateStatus {
    NOT_FOUND,
    ALL_LOCKED,
    ALL_CONFIRMED,
    ALL_RELEASED,
    INCONSISTENT
}
