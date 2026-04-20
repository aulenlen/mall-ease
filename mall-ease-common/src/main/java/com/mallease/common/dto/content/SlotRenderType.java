package com.mallease.common.dto.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/** 槽位渲染类型。 */
public enum SlotRenderType {

    SWIPER("SWIPER"),
    ARTICLE_LIST("ARTICLE_LIST");

    private final String code;

    SlotRenderType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static SlotRenderType fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的槽位渲染类型: " + code));
    }
}
