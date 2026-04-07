package com.mallease.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderSource {
    NORMAL(0, "普通订单"),
    FLASH(1, "秒杀订单");

    private final int code;
    private final String desc;
}
