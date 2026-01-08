package com.mallease.product.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PMS 模块 Redis Key 枚举
 * 每个枚举项包含：前缀 + TTL + 描述，确保 Key 和过期时间绑定
 *
 * @author: Aulen
 * @create: 2025-01-03
 */
@Getter
@AllArgsConstructor
public enum RedisKey {

    /**
     * SPU详情缓存（String结构）
     * Key格式: product:spu:detail:{spuId}
     */
    SPU_DETAIL("product:spu:detail:", 3600L, "SPU详情缓存"),

    /**
     * SKU库存缓存（Hash结构）
     * Key格式: pms:spu:sku:stock:{spuId}
     * HashKey: skuId
     */
    SPU_SKU_STOCK("product:spu:sku:stock:", 3600L, "SKU库存缓存"),

    /**
     * 金刚区导航分类（String结构，存储 List）
     * Key格式: pms:category:nav（固定Key）
     */
    CATEGORY_NAV("product:category:nav", 86400L, "金刚区导航分类"),

    /**
     * 分类商品列表缓存
     * Key格式: pms:category:list:{categoryId}
     */
    CATEGORY_LIST("product:category:list:", 7200L, "分类商品列表"),

    /**
     * 品牌商品列表缓存
     * Key格式: pms:brand:list:{brandId}
     */
    BRAND_LIST("product:brand:list:", 7200L, "品牌商品列表"),
    ;

    /**
     * Key 前缀
     */
    private final String prefix;

    /**
     * 过期时间（秒），-1 表示永不过期
     */
    private final long ttl;

    /**
     * 描述（用于日志/监控）
     */
    private final String desc;

    /**
     * 构建完整 Key（无参数，适用于固定Key）
     *
     * @return 完整的 Redis Key
     */
    public String key() {
        return prefix;
    }

    /**
     * 构建完整 Key（单参数）
     *
     * @param id 业务ID
     * @return 完整的 Redis Key
     */
    public String key(Object id) {
        return prefix + id;
    }

    /**
     * 构建完整 Key（多参数，用 : 分隔）
     *
     * @param parts 多个参数
     * @return 完整的 Redis Key
     */
    public String key(Object... parts) {
        if (parts == null || parts.length == 0) {
            return prefix;
        }
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i]);
            if (i < parts.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
}