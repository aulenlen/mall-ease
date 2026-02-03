package com.mallease.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存释放状态枚举（订单级别）
 *
 * @author: Aulen
 * @create: 2026-02-02
 */
@Getter
@AllArgsConstructor
public enum StockReleaseStatus {

    /**
     * 未触发/不需要（正常待支付、已支付等）
     */
    NOT_TRIGGERED(0, "未触发"),

    /**
     * 待释放（订单已取消/关闭，已触发释放但未确认成功）
     */
    PENDING_RELEASE(1, "待释放"),

    /**
     * 已释放
     */
    RELEASED(2, "已释放"),

    /**
     * 释放失败（需要重试/人工关注）
     */
    RELEASE_FAILED(3, "释放失败");

    private final int code;
    private final String desc;
}
