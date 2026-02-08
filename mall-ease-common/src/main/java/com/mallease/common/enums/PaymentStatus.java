package com.mallease.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PaymentStatus {
    PENDING(1, "待支付"),
    SUCCESS(2, "支付成功"),
    CLOSED(3, "已关闭"),
    FAILED(4, "支付失败");

    private final Integer code;
    private final String desc;
}
