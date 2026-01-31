package com.mallease.common.enums;

import java.util.Arrays;

/**
 * 订单状态枚举
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
public enum OrderStatus {
    PENDING_SHIPMENT(1, "待发货"),
    PENDING_RECEIPT(2, "待收货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消"),
    PENDING_PAYMENT(5, "待支付"),
    PAID(6, "待支付");

    private final int code;
    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 订单状态枚举
     */
    public static OrderStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据状态码获取描述
     *
     * @param code 状态码
     * @return 状态描述
     */
    public static String getDescriptionByCode(int code) {
        OrderStatus status = fromCode(code);
        return status != null ? status.description : "未知状态";
    }
}