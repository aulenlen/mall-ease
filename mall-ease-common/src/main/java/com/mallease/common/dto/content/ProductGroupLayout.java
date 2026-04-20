package com.mallease.common.dto.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/** 商品组布局。 */
public enum ProductGroupLayout {

    GRID("grid"),
    LIST("list"),
    CAROUSEL("carousel");

    private final String code;

    ProductGroupLayout(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static ProductGroupLayout fromCode(String code) {
        return Arrays.stream(values())
                .filter(layout -> layout.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的商品组布局: " + code));
    }
}
