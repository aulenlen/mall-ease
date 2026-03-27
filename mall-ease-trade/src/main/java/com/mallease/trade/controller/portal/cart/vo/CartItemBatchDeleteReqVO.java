package com.mallease.trade.controller.portal.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量删除购物车命令
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Schema(description = "批量删除购物车命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemBatchDeleteReqVO {

    @Schema(description = "购物车项ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "购物车项ID列表不能为空")
    @Size(max = 100, message = "单次删除不能超过100条")
    private List<Long> ids;
}