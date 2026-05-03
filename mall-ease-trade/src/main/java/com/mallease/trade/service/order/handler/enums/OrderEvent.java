package com.mallease.trade.service.order.handler.enums;

public enum OrderEvent {
    BEFORE_CREATE,
    AFTER_PERSISTED,
    AFTER_CREATED,
    AFTER_PAID,
    BEFORE_CANCEL,
    AFTER_CANCELLED,
    AFTER_EXPIRED,
    RETRY_CONFIRM,
    RETRY_RELEASE
}
