package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 库存页 SKU 子表记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "库存页 SKU 子表记录")
public class InventoryRecordRespVO {

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SKU 编码")
    private String skuCode;

    @Schema(description = "可用库存")
    private Integer stock;

    @Schema(description = "锁定库存")
    private Integer lockStock;

    @Schema(description = "销量")
    private Integer sale;

    @Schema(description = "库存预警值")
    private Integer lowStock;

    @Schema(description = "库存状态: 0-无货, 1-有货, 2-预售")
    private Integer stockStatus;

    @Schema(description = "规格 JSON")
    private String attrValues;

    @Schema(description = "规格列表")
    private List<SpecVO> specs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "规格项")
    public static class SpecVO {

        @Schema(description = "属性 ID")
        private Long attrId;

        @Schema(description = "属性名")
        private String attrName;

        @Schema(description = "属性值")
        private String attrValue;
    }
}
