package com.mallease.common.constant;

/**
 * 秒杀缓存 Redis Key
 */
public final class FlashRedisKeys {

    private static final String ROUTE_KEY_PREFIX = "flash:route:spu:";
    private static final String OVERLAY_KEY_PREFIX = "flash:overlay:session:";
    private static final String DETAIL_KEY_PREFIX = "flash:detail:session:";
    private static final String STOCK_KEY_PREFIX = "flash:stock:session:";

    private FlashRedisKeys() {
    }

    public static String routeKey(Long spuId) {
        return ROUTE_KEY_PREFIX + spuId;
    }

    public static String overlayKey(Long sessionId, Long spuId) {
        return OVERLAY_KEY_PREFIX + sessionId + ":spu:" + spuId;
    }

    public static String detailKey(Long sessionId, Long spuId) {
        return DETAIL_KEY_PREFIX + sessionId + ":spu:" + spuId;
    }

    public static String stockKey(Long sessionId, Long skuId) {
        return STOCK_KEY_PREFIX + sessionId + ":sku:" + skuId;
    }
}