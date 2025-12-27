package com.mallease.pms.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU阶梯价格表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class PmsSkuLadder {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SKU ID（关联 pms_sku.id）
     */
    private Long skuId;

    /**
     * 满足的商品数量
     */
    private Integer count;

    /**
     * 折扣（0.8 表示 8 折）
     */
    private BigDecimal discount;

    /**
     * 折后价格
     */
    private BigDecimal price;

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