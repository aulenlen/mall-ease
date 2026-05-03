package com.mallease.trade.service.order.handler.exception;

public class StockLockUnknownException extends RuntimeException {

    public StockLockUnknownException(String message, Throwable cause) {
        super(message, cause);
    }
}
