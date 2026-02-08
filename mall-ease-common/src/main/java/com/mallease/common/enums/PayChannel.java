package com.mallease.common.enums;

import lombok.Getter;

@Getter
public enum PayChannel {
    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信支付"),
    MOCK(9, "模拟支付");

    private Integer code;
    private String desc;
    PayChannel(Integer code, String desc) {}
}
