package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存页 tab 统计
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "库存页 tab 统计")
public class InventoryTabTotalsRespVO {

    @Schema(description = "全部")
    private Long all;

    @Schema(description = "低库存")
    private Long warning;

    @Schema(description = "缺货")
    private Long empty;

    @Schema(description = "预售")
    private Long presale;
}
