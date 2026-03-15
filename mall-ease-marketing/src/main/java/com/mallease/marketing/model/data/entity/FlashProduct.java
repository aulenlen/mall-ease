package com.mallease.marketing.model.data.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品关联表
 *
 * @author: Aulen
 * @create: 2026-01-04
 */
@Data
public class FlashProduct {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 秒杀活动ID
     */
    private Long flashActivityId;

    /**
     * 秒杀场次ID
     */
    private Long flashSessionId;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 秒杀价格
     */
    private BigDecimal flashPrice;

    /**
     * 秒杀库存
     */
    private Integer flashStock;

    /**
     * 每人限购数量
     */
    private Integer flashLimit;

    /**
     * 排序（越小越靠前）
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;
}