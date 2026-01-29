package com.mallease.trade.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车汇总视图对象
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Schema(description = "购物车汇总")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartVO {

    @Schema(description = "购物车项列表")
    private List<CartItemVO> items;

    @Schema(description = "有效商品数量（件数）")
    private Integer validCount;

    @Schema(description = "无效商品数量（件数）")
    private Integer invalidCount;

    @Schema(description = "已选中商品数量（件数）")
    private Integer checkedCount;

    @Schema(description = "已选中商品总金额")
    private BigDecimal checkedAmount;

    @Schema(description = "是否全选")
    private Boolean allChecked;

    @Schema(description = "是否有价格变动的商品")
    private Boolean hasPriceChanged;

    @Schema(description = "是否有无货商品")
    private Boolean hasOutOfStock;
}
