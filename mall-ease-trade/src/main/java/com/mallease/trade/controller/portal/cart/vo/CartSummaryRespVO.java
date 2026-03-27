package com.mallease.trade.controller.portal.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车汇总视图对象。
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Schema(description = "购物车汇总")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartSummaryRespVO {

    @Schema(description = "已选中商品总金额")
    private BigDecimal checkedAmount;
}
