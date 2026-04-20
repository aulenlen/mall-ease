package com.mallease.common.dto.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/** 槽位投放项类型。 */
public enum SlotItemType {

    CARD("CARD"),
    ARTICLE("ARTICLE");

    private final String code;

    SlotItemType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static SlotItemType fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的槽位投放项类型: " + code));
    }
}
