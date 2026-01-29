package com.mallease.trade.model.data.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车明细
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Data
public class CartItem {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SPU ID（冗余，便于按商品聚合）
     */
    private Long spuId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 选中状态: 0-未选中, 1-已选中
     */
    private Integer checked;

    /**
     * 商品名称（冗余）
     */
    private String spuName;

    /**
     * SKU图片URL（冗余）
     */
    private String skuPic;

    /**
     * SKU规格属性（冗余，如：颜色:红色;尺码:XL）
     */
    private String skuAttrs;

    /**
     * 加入时单价（冗余）
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}