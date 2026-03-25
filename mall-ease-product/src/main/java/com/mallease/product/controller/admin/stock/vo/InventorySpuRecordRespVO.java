package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 库存页 SPU 聚合记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "库存页 SPU 聚合记录")
public class InventorySpuRecordRespVO {

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SPU 名称")
    private String spuName;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "SKU 数量")
    private Integer skuCount;

    @Schema(description = "总可用库存")
    private Integer totalAvailableStock;

    @Schema(description = "总锁定库存")
    private Integer totalLockStock;

    @Schema(description = "总销量")
    private Integer totalSale;

    @Schema(description = "低库存 SKU 数")
    private Integer warningSkuCount;

    @Schema(description = "缺货 SKU 数")
    private Integer emptySkuCount;

    @Schema(description = "预售 SKU 数")
    private Integer presaleSkuCount;

    @Schema(description = "SKU 子表记录")
    private List<InventoryRecordRespVO> records;
}
