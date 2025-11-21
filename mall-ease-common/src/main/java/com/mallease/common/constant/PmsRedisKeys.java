package com.mallease.common.constant;

/**
 * Redis 键前缀集中管理，避免硬编码字符串。
 */
public final class PmsRedisKeys {
    private PmsRedisKeys() {
    }

    /**
     * 商品详情缓存前缀.
     */
    public static final String PRODUCT_DETAIL_PREFIX = "product:detail:";

    /**
     * 分类商品列表缓存前缀.
     */
    public static final String PRODUCT_CATEGORY_LIST_PREFIX = "product:list:category:";

    /**
     * 品牌商品列表缓存前缀.
     */
    public static final String PRODUCT_BRAND_LIST_PREFIX = "product:list:brand:";

    /**
     * 商品缓存时间
     */
    static final long PRODUCT_DETAIL_CACHE_EXPIRE_SECONDS = 3600L;

    public static String productDetailKey(Long productId) {
        return PRODUCT_DETAIL_PREFIX + productId;
    }

    public static String categoryListKey(Long categoryId) {
        return PRODUCT_CATEGORY_LIST_PREFIX + categoryId;
    }

    public static String brandListKey(Long brandId) {
        return PRODUCT_BRAND_LIST_PREFIX + brandId;
    }

    public static long getProductDetailCacheExpireSeconds() {
        return PRODUCT_DETAIL_CACHE_EXPIRE_SECONDS;
    }
}
