package com.mallease.trade.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项视图对象
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Schema(description = "购物车项")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemVO {

    @Schema(description = "购物车项ID")
    private Long id;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "选中状态: 0-未选中, 1-已选中")
    private Integer checked;

    @Schema(description = "商品名称")
    private String spuName;

    @Schema(description = "SKU图片URL")
    private String skuPic;

    @Schema(description = "SKU规格属性（如：颜色:红色;尺码:XL）")
    private String skuAttrs;

    @Schema(description = "加入时单价")
    private BigDecimal price;

    @Schema(description = "当前实时价格")
    private BigDecimal currentPrice;

    @Schema(description = "价格是否变动")
    private Boolean priceChanged;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "是否有货")
    private Boolean inStock;

    @Schema(description = "商品是否有效（未下架/未删除）")
    private Boolean valid;

    @Schema(description = "失效原因（商品下架/已删除等）")
    private String invalidReason;

    @Schema(description = "小计金额（quantity * currentPrice）")
    private BigDecimal subtotal;

    @Schema(description = "加入购物车时间")
    private LocalDateTime createTime;
}
