package com.mallease.common.enums;

import lombok.Getter;

@Getter
public enum PayChannel {
    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信支付"),
    MOCK(9, "模拟支付");

    private final Integer code;
    private final String desc;

    PayChannel(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PayChannel of(Integer code) {
        for (PayChannel channel : values()) {
            if (channel.code.equals(code)) {
                return channel;
            }
        }
        return null;
    }
}
