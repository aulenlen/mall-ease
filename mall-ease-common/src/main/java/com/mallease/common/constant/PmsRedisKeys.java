package com.mallease.common.constant;

/**
 * Redis 键前缀集中管理，避免硬编码字符串。
 */
public final class PmsRedisKeys {


    private PmsRedisKeys() {
    }

    // ==================== SPU 缓存键 ====================

    /**
     * SPU详情缓存前缀
     */
    public static final String SPU_DETAIL_PREFIX = "spu:detail:";

    /**
     * SKU库存缓存前缀（Hash结构）
     */
    public static final String SPU_SKU_STOCK_PREFIX = "spu:sku:stock:";

    // ==================== 旧版商品缓存键（待废弃） ====================

    /**
     * 商品详情缓存前缀（旧版，兼容已有代码）
     * @deprecated 使用 SPU_DETAIL_PREFIX 替代
     */
    @Deprecated
    public static final String PRODUCT_DETAIL_PREFIX = "product:detail:";

    /**
     * 库存缓存前缀（旧版，兼容已有代码）
     * @deprecated 使用 SPU_SKU_STOCK_PREFIX 替代
     */
    @Deprecated
    public static final String PRODUCT_SKU_STOCK_PREFIX = "product:sku:stock:";

    /**
     * 分类商品列表缓存前缀
     */
    public static final String PRODUCT_CATEGORY_LIST_PREFIX = "product:list:category:";

    /**
     * 品牌商品列表缓存前缀
     */
    public static final String PRODUCT_BRAND_LIST_PREFIX = "product:list:brand:";

    // ==================== 缓存过期时间 ====================

    /**
     * SPU详情缓存过期时间（秒）
     */
    public static final long SPU_DETAIL_CACHE_EXPIRE_SECONDS = 3600L;

    /**
     * SKU库存缓存过期时间（秒）
     */
    public static final long SKU_STOCK_CACHE_EXPIRE_SECONDS = 3600L;

    /**
     * 商品缓存时间（旧版，兼容已有代码）
     * @deprecated 使用 SPU_DETAIL_CACHE_EXPIRE_SECONDS 替代
     */
    @Deprecated
    static final long PRODUCT_DETAIL_CACHE_EXPIRE_SECONDS = 3600L;

    /**
     * sku库存缓存时间（旧版，兼容已有代码）
     * @deprecated 使用 SKU_STOCK_CACHE_EXPIRE_SECONDS 替代
     */
    @Deprecated
    public static final long SKU_STOCK_DEFAULT_EXPIRE_SECONDS = 3600L;

    // ==================== 辅助方法 ====================

    /**
     * 构建SPU详情缓存Key
     */
    public static String spuDetailKey(Long spuId) {
        return SPU_DETAIL_PREFIX + spuId;
    }

    /**
     * 构建SKU库存缓存Key
     */
    public static String spuSkuStockKey(Long spuId) {
        return SPU_SKU_STOCK_PREFIX + spuId;
    }

    /**
     * 构建商品详情Key（旧版，兼容已有代码）
     * @deprecated 使用 spuDetailKey 替代
     */
    @Deprecated
    public static String productDetailKey(Long productId) {
        return PRODUCT_DETAIL_PREFIX + productId;
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

    /**
     * 获取商品详情缓存过期时间（旧版，兼容已有代码）
     * @deprecated 使用 getSpuDetailCacheExpireSeconds 替代
     */
    @Deprecated
    public static long getProductDetailCacheExpireSeconds() {
        return PRODUCT_DETAIL_CACHE_EXPIRE_SECONDS;
    }
}
