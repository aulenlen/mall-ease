package com.mallease.product.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品已发布快照
 *
 * <p>用于保存前台当前可见的商品静态快照，作为 Redis 和 ES 的稳定回源数据。</p>
 *
 * @author: Aulen
 * @create: 2026-03-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpuSnapshot {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 已发布版本号
     */
    private Integer version;

    /**
     * 发布状态：0-下架 1-上架
     */
    private Integer publishStatus;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品副标题
     */
    private String subTitle;

    /**
     * 商品主图
     */
    private String pic;

    /**
     * 最低价
     */
    private BigDecimal minPrice;

    /**
     * 最高价
     */
    private BigDecimal maxPrice;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类路径
     */
    private String categoryIds;

    /**
     * 快照 JSON
     */
    private String snapshotJson;

    /**
     * 快照摘要
     */
    private String snapshotHash;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
