package com.mallease.trade.controller.portal.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前台添加购物车命令。
 *
 * @author: Aulen
 * @create: 2026-01-28
 */
@Schema(description = "前台添加购物车命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemAddReqVO {

    @Schema(description = "SKU ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    @Schema(description = "商品数量", defaultValue = "1")
    @Min(value = 1, message = "商品数量必须大于0")
    @Builder.Default
    private Integer quantity = 1;
}
