package com.mallease.trade.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Getter
@AllArgsConstructor
public enum OperationType {

    SHIP(1, "发货"),
    FORCE_CANCEL(2, "强制取消"),
    UPDATE_ADDRESS(3, "修改地址"),
    UPDATE_REMARK(4, "修改备注"),
    ADJUST_AMOUNT(5, "调整金额"),
    REFUND(6, "退款");

    private final int code;
    private final String description;
}
