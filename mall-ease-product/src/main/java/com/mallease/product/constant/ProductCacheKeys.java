package com.mallease.product.constant;

/**
 * 商品模块 Redis Key 定义。
 * 只收口商品详情与库存两类复杂缓存，避免业务层散落拼接 key。
 */
public final class ProductCacheKeys {

    private static final String SPU_SNAPSHOT_PREFIX = "product:spu:snapshot:";
    private static final long SPU_SNAPSHOT_TTL_SECONDS = 3600L;

    private static final String SPU_SKU_STOCK_PREFIX = "product:spu:sku:stock:";
    private static final long SPU_SKU_STOCK_TTL_SECONDS = 3600L;

    private ProductCacheKeys() {
    }

    public static String spuDetailKey(Long spuId) {
        return SPU_SNAPSHOT_PREFIX + spuId;
    }

    public static String spuStockKey(Long spuId) {
        return SPU_SKU_STOCK_PREFIX + spuId;
    }

    public static String spuStockPattern() {
        return SPU_SKU_STOCK_PREFIX + "*";
    }

    public static String spuDetailPrefix() {
        return SPU_SNAPSHOT_PREFIX;
    }

    public static String spuStockPrefix() {
        return SPU_SKU_STOCK_PREFIX;
    }

    public static long spuDetailTtlSeconds() {
        return SPU_SNAPSHOT_TTL_SECONDS;
    }

    public static long spuStockTtlSeconds() {
        return SPU_SKU_STOCK_TTL_SECONDS;
    }
}
