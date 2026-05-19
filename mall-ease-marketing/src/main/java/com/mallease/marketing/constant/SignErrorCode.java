package com.mallease.marketing.constant;

import com.mallease.common.api.IErrorCode;

/**
 * 签到业务错误码。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
public enum SignErrorCode implements IErrorCode {

    ALREADY_SIGNED(600101, "该日期已签到"),
    SIGN_RULE_NOT_FOUND(600105, "签到规则不存在"),
    SIGN_RULE_DUPLICATED(600106, "连续天数规则已存在"),
    MEMBER_NOT_LOGIN(600107, "会员未登录");

    private final Integer code;
    private final String message;

    SignErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
