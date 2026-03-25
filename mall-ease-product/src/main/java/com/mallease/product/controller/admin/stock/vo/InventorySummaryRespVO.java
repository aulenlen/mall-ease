package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 库存页卡片统计
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "库存页卡片统计")
public class InventorySummaryRespVO {

    @Schema(description = "SPU 数量")
    private Long spuCount;

    @Schema(description = "SKU 数量")
    private Long skuCount;

    @Schema(description = "低库存 SPU 数量")
    private Long warningSpuCount;

    @Schema(description = "缺货 SPU 数量")
    private Long emptySpuCount;

    @Schema(description = "预售 SPU 数量")
    private Long presaleSpuCount;
}
