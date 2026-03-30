package com.mallease.trade.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付回调处理状态枚举
 *
 * @author: Aulen
 * @create: 2026-03-31
 */
@Getter
@AllArgsConstructor
public enum PaymentNotifyProcessStatus {

    PENDING(1, "待处理"),
    SUCCESS(2, "处理成功"),
    DUPLICATE(3, "重复忽略"),
    FAILED(4, "处理失败");

    private final int code;
    private final String description;
}