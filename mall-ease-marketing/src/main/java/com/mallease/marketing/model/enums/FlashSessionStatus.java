package com.mallease.marketing.model.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum FlashSessionStatus {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final int code;
    private final String desc;

    FlashSessionStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean codeEquals(Integer code) {
        return Objects.equals(this.code, code);
    }
}
