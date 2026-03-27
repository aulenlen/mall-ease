package com.mallease.trade.controller.portal.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车分页项视图对象。
 *
 * @author: Aulen
 * @create: 2026-03-12
 */
@Schema(description = "购物车分页项")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartPageItemRespVO {

    @Schema(description = "购物车项ID")
    private Long id;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "选中状态，0-未选中，1-已选中")
    private Integer checked;

    @Schema(description = "商品名称")
    private String spuName;

    @Schema(description = "SKU图片URL")
    private String skuPic;

    @Schema(description = "SKU规格属性")
    private String skuAttrs;

    @Schema(description = "加入购物车时价格")
    private BigDecimal price;

    @Schema(description = "当前展示价格")
    private BigDecimal currentPrice;

    @Schema(description = "是否有货")
    private Boolean inStock;

    @Schema(description = "当前页小计金额")
    private BigDecimal subtotal;

    @Schema(description = "加入购物车时间")
    private LocalDateTime createTime;
}
