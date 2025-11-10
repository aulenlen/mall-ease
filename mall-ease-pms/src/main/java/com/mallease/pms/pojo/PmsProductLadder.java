package com.mallease.pms.pojo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品阶梯价格表(只针对同商品)
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Data
public class PmsProductLadder {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 满足的商品数量
     */
    private Integer count;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 折后价格
     */
    private BigDecimal price;
}

