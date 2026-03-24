package com.mallease.product.service.stock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存预占状态枚举
 *
 * @author: Aulen
 * @create: 2026-02-01
 */
@Getter
@AllArgsConstructor
public enum ReservationStatus {

    /**
     * 已锁定（下单成功，等待支付）
     */
    LOCKED(1, "已锁定"),

    /**
     * 已释放（取消订单/支付超时）
     */
    RELEASED(2, "已释放"),

    /**
     * 已确认（支付成功，库存实扣）
     */
    CONFIRMED(3, "已确认");

    private final int code;
    private final String desc;
}
