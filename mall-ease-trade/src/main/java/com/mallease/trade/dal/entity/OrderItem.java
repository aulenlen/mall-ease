package com.mallease.trade.dal.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品表实体
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
@Data
@Builder
public class OrderItem {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品名称
     */
    private String spuName;

    /**
     * SKU图片
     */
    private String skuPic;

    /**
     * SKU规格属性
     */
    private String skuAttrs;

    /**
     * 下单时单价
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}