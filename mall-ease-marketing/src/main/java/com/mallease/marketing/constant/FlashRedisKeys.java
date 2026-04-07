package com.mallease.marketing.constant;

/**
 * 秒杀缓存 Redis Key
 */
public final class FlashRedisKeys {

    private static final String DETAIL_KEY_PREFIX = "flash:detail:session:";
    private static final String SELECTOR_KEY_PREFIX = "flash:selector:session:";
    private static final String STOCK_KEY_PREFIX = "flash:stock:session:";
    private static final String LIMIT_KEY_PREFIX = "flash:limit:session:";
    private static final String SESSIONS_DATE_PREFIX = "flash:sessions:date:";
    private static final String PRODUCTS_SESSION_PREFIX = "flash:products:session:";
    private static final String USER_BOUGHT_PREFIX = "flash:bought:session:";
    private static final String SESSION_META_PREFIX = "flash:meta:session:";
    private static final String SESSION_KEY_PREFIX = "flash:session:";
    private static final String ORDER_SNAPSHOT_KEY = "flash:order:snapshot:";

    private FlashRedisKeys() {
    }

    public static String orderSnapshotKey(Long userId, String requestId) {
        return ORDER_SNAPSHOT_KEY + userId + ":req:" + requestId;
    }

    public static String userBoughtKey(Long sessionId, Long skuId, Long userId) {
        return USER_BOUGHT_PREFIX + sessionId + ":sku:" + skuId + ":uid:" + userId;
    }

    public static String sessionMetaKey(Long sessionId) {
        return SESSION_META_PREFIX + sessionId;
    }

    public static String sessionKey(Long sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
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

    public static String limitKey(Long sessionId, Long skuId) {
        return LIMIT_KEY_PREFIX + sessionId + ":sku:" + skuId;
    }
}