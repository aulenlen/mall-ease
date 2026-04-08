package com.mallease.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderSource {
    NORMAL(0, "普通订单"),
    FLASH(1, "秒杀订单");

    private final int code;
    private final String desc;

    public static OrderSource fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(source -> source.code == code)
                .findFirst()
                .orElse(null);
    }
}
