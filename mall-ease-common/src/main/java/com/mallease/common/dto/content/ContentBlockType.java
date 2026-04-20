package com.mallease.common.dto.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/** 正文块类型。 */
public enum ContentBlockType {

    HEADING("heading"),
    PARAGRAPH("paragraph"),
    IMAGE("image"),
    LEAD("lead"),
    SECTION("section"),
    PRODUCT_GROUP("product_group");

    private final String code;

    ContentBlockType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static ContentBlockType fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的正文块类型: " + code));
    }
}
