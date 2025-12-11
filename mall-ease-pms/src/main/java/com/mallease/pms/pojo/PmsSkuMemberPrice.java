package com.mallease.pms.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU会员价格表
 * 说明：存储不同会员等级的 SKU 专属价格
 * 替代旧表：pms_member_price（改为关联 sku_id）
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class PmsSkuMemberPrice {
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