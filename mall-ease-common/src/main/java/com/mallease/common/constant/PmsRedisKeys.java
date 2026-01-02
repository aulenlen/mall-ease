package com.mallease.common.constant;

/**
 * Redis 键前缀集中管理，避免硬编码字符串。
 */
public final class PmsRedisKeys {


    private PmsRedisKeys() {
    }

    /**
     * 金刚区专用key
     */
    public static final String CATEGORY_NAV = "category:nav";

    /**
     * SPU详情缓存前缀
     */
    public static final String SPU_DETAIL_PREFIX = "spu:detail:";

    /**
     * SKU库存缓存前缀（Hash结构）
     */
    public static final String SPU_SKU_STOCK_PREFIX = "spu:sku:stock:";

    /**
     * 分类商品列表缓存前缀
     */
    public static final String PRODUCT_CATEGORY_LIST_PREFIX = "product:list:category:";

    /**
     * 品牌商品列表缓存前缀
     */
    public static final String PRODUCT_BRAND_LIST_PREFIX = "product:list:brand:";

    /**
     * SPU详情缓存过期时间（秒）
     */
    public static final long SPU_DETAIL_CACHE_EXPIRE_SECONDS = 3600L;

    /**
     * SKU库存缓存过期时间（秒）
     */
    public static final long SKU_STOCK_CACHE_EXPIRE_SECONDS = 3600L;

    /**
     * 金刚区key缓存过期时间
     */
    private static final long CATEGORY_NAV_EXPIRE_SECONDS = 24 * 60 * 60;

    /**
     * 构建SPU详情缓存Key
     */
    public static String spuDetailKey(Long spuId) {
        return SPU_DETAIL_PREFIX + spuId;
    }

    public static String categoryNav() {
        return CATEGORY_NAV;
    }

    public static long getCategoryNavExpireSeconds() {
        return CATEGORY_NAV_EXPIRE_SECONDS;
    }

    /**
     * 构建SKU库存缓存Key
     */
    public static String spuSkuStockKey(Long spuId) {
        return SPU_SKU_STOCK_PREFIX + spuId;
    }

    public static String categoryListKey(Long categoryId) {
        return PRODUCT_CATEGORY_LIST_PREFIX + categoryId;
    }

    public static String brandListKey(Long brandId) {
        return PRODUCT_BRAND_LIST_PREFIX + brandId;
    }

    /**
     * 获取SPU详情缓存过期时间
     */
    public static long getSpuDetailCacheExpireSeconds() {
        return SPU_DETAIL_CACHE_EXPIRE_SECONDS;
    }

    /**
     * 获取SKU库存缓存过期时间
     */
    public static long getSkuStockCacheExpireSeconds() {
        return SKU_STOCK_CACHE_EXPIRE_SECONDS;
    }
}
