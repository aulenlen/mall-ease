package com.mallease.common.exception;

import com.mallease.common.api.IErrorCode;

/**
 * @author: Aulen
 * @description: 断言工具，用于抛出异常
 * @create: 2025-11-07 20:16
 **/
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(IErrorCode errorCode) {
        throw new ApiException(errorCode);
    }
}
