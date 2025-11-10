package com.mallease.pms.pojo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品满减表(只针对同商品)
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Data
public class PmsProductFullReduction {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 满减价格
     */
    private BigDecimal fullPrice;

    /**
     * 减价
     */
    private BigDecimal reducePrice;
}

