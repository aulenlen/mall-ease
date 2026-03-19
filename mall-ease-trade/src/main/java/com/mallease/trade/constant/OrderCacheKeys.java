package com.mallease.trade.constant;

import static com.mallease.trade.constant.OrderConstant.SNAPSHOT_EXPIRE_SECONDS;

/**
 * 订单模块 Redis Key 定义。
 */
public final class OrderCacheKeys {

    private static final String SNAPSHOT_KEY_PREFIX = "order:snapshot:";

    private OrderCacheKeys() {
    }

    public static String snapshotKey(String requestId) {
        return SNAPSHOT_KEY_PREFIX + requestId;
    }

    public static long snapshotTtlSeconds() {
        return SNAPSHOT_EXPIRE_SECONDS;
    }
}
