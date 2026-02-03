package com.mallease.trade.constant;

/**
 * 订单相关常量
 *
 * @author: Aulen
 * @create: 2026-02-03
 */
public final class OrderConstant {

    private OrderConstant() {
    }

    /**
     * 订单支付超时时间（分钟）
     */
    public static final int PAYMENT_TIMEOUT_MINUTES = 30;

    /**
     * 订单快照过期时间（秒）
     */
    public static final long SNAPSHOT_EXPIRE_SECONDS = 30 * 60;

}
