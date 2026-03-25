package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存页统计响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "库存页统计响应")
public class InventoryStatsRespVO {

    @Schema(description = "卡片统计")
    private InventorySummaryRespVO summary;

    @Schema(description = "tab 统计")
    private InventoryTabTotalsRespVO tabTotals;
}
