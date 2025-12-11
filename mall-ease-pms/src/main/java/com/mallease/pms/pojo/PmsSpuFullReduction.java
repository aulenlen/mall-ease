package com.mallease.pms.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SPU满减表
 * 说明：存储 SPU 级别的满减规则（满足金额减免金额）
 * 替代旧表：pms_product_full_reduction（改为关联 spu_id）
 * 绑定SPU原因：满减是活动类促销，对整个商品生效，不区分具体规格
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class PmsSpuFullReduction {
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