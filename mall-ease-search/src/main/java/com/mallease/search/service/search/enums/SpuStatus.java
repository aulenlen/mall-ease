package com.mallease.search.service.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SpuStatus {
    PUBLISH(1, "上架"),
    UNPUBLISH(0, "下架");
    private final int code;
    private final String desc;
}
