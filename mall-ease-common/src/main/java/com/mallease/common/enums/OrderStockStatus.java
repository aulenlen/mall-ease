package com.mallease.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单库存处理状态。
 */
@Getter
@AllArgsConstructor
public enum OrderStockStatus {

    INIT(0, "未开始"),

    LOCKING(1, "锁库中"),

    LOCKED(2, "已锁定"),

    LOCK_UNKNOWN(3, "锁库结果未知"),

    LOCK_FAILED(4, "锁库失败"),

    CONFIRMING(5, "确认扣减中"),

    CONFIRMED(6, "已确认扣减"),

    CONFIRM_FAILED(7, "确认扣减失败"),

    RELEASING(8, "释放中"),

    RELEASED(9, "已释放"),

    RELEASE_FAILED(10, "释放失败");

    private final int code;
    private final String desc;

    public boolean needsLockReconcile() {
        return this == LOCKING || this == LOCK_UNKNOWN;
    }

    public boolean needsConfirmRetry() {
        return this == CONFIRMING || this == CONFIRM_FAILED;
    }

    public boolean needsReleaseRetry() {
        return this == RELEASING || this == RELEASE_FAILED;
    }
}
