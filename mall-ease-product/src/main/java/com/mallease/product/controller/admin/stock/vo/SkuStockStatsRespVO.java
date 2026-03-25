package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台库存统计响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台库存统计响应")
public class SkuStockStatsRespVO {

    @Schema(description = "库存记录总数")
    private Long totalCount;

    @Schema(description = "有货数量")
    private Long inStockCount;

    @Schema(description = "低库存数量")
    private Long lowStockCount;

    @Schema(description = "无货数量")
    private Long outOfStockCount;
}
