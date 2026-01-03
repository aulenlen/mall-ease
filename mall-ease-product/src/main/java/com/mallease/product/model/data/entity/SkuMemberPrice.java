package com.mallease.product.model.data.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU会员价格表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class SkuMemberPrice {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SKU ID（关联 pms_sku.id）
     */
    private Long skuId;

    /**
     * 会员等级ID
     */
    private Long memberLevelId;

    /**
     * 会员等级名称（冗余）
     */
    private String memberLevelName;

    /**
     * 会员价格
     */
    private BigDecimal memberPrice;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}