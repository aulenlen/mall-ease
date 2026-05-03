package com.mallease.trade.service.order.handler.exception;

public class StockLockFailedException extends RuntimeException {

    public StockLockFailedException(String message) {
        super(message);
    }
}
