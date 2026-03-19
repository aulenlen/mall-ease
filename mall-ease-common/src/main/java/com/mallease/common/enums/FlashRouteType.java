package com.mallease.common.enums;

import lombok.Getter;

@Getter
public enum FlashRouteType {

    NORMAL(0, "非热点秒杀"),
    HOT(1, "热点秒杀");

    private final Integer code;
    private final String desc;

    FlashRouteType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean codeEquals(Integer code) {
        return code != null && this.code.equals(code);
    }

    public static boolean isHot(Integer code) {
        return HOT.codeEquals(code);
    }

    public static boolean isValid(Integer code) {
        return NORMAL.codeEquals(code) || HOT.codeEquals(code);
    }
}
