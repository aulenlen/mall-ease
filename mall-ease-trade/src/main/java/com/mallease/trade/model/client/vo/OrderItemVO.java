package com.mallease.trade.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单商品视图对象
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
@Schema(description = "订单商品信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemVO {

    @Schema(description = "订单商品ID")
    private Long id;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "商品名称")
    private String spuName;

    @Schema(description = "SKU图片")
    private String skuPic;

    @Schema(description = "SKU规格属性")
    private String skuAttrs;

    @Schema(description = "下单时单价")
    private BigDecimal price;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "小计金额")
    private BigDecimal subtotal;
}