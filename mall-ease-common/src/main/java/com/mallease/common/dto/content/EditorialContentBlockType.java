package com.mallease.common.dto.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/** 编辑精选正文块类型。 */
public enum EditorialContentBlockType {

    HEADING("heading"),
    PARAGRAPH("paragraph"),
    IMAGE("image");

    private final String code;

    EditorialContentBlockType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static EditorialContentBlockType fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的正文块类型: " + code));
    }
}
