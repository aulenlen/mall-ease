package com.mallease.product.model.data.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SPU满减表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class SpuFullReduction {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID（关联 pms_spu.id）
     */
    private Long spuId;

    /**
     * 满足金额
     */
    private BigDecimal fullPrice;

    /**
     * 减免金额
     */
    private BigDecimal reducePrice;

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