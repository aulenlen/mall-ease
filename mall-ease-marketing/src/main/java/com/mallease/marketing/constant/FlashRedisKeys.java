package com.mallease.marketing.constant;

/**
 * 秒杀缓存 Redis Key
 */
public final class FlashRedisKeys {

    private static final String DETAIL_KEY_PREFIX = "flash:detail:session:";
    private static final String SELECTOR_KEY_PREFIX = "flash:selector:session:";
    private static final String STOCK_KEY_PREFIX = "flash:stock:session:";
    private static final String SESSIONS_DATE_PREFIX = "flash:sessions:date:";
    private static final String PRODUCTS_SESSION_PREFIX = "flash:products:session:";

    private FlashRedisKeys() {
    }
    
    public static String sessionsKey(String dateStr) {
        return SESSIONS_DATE_PREFIX + dateStr;
    }
    
    public static String productsKey(Long sessionId) {
        return PRODUCTS_SESSION_PREFIX + sessionId;
    }

    public static String detailKey(Long sessionId, Long spuId) {
        return DETAIL_KEY_PREFIX + sessionId + ":spu:" + spuId;
    }

    public static String selectorKey(Long sessionId, Long spuId) {
        return SELECTOR_KEY_PREFIX + sessionId + ":spu:" + spuId;
    }

    public static String stockKey(Long sessionId, Long skuId) {
        return STOCK_KEY_PREFIX + sessionId + ":sku:" + skuId;
    }
}
