package com.mallease.product.model.data.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品会员价格表
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
public class MemberPrice {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 会员等级ID
     */
    private Long memberLevelId;

    /**
     * 会员价格
     */
    private BigDecimal memberPrice;

    /**
     * 会员等级名称
     */
    private String memberLevelName;
}