package com.mallease.marketing.enums;

import lombok.Getter;

@Getter
public enum FlashTimeStatus {
    NOT_STARTED(0, "未开始"),
    ONGOING(1, "进行中"),
    ENDED(2, "已结束");

    private final int code;
    private final String desc;

    FlashTimeStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
