package com.mallease.trade.controller.portal.cart.vo;

import com.mallease.common.api.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车分页结果。
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Schema(description = "购物车分页结果")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartPageRespVO {

    @Schema(description = "购物车分页列表")
    private Page<CartPageItemRespVO> page;

    @Schema(description = "购物车汇总")
    private CartSummaryRespVO summary;
}
