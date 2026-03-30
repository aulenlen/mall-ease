package com.mallease.product.controller.portal.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "前台规格选择器响应")
public class ProductSelectorRespVO {

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "默认选中信息")
    private ProductDetailRespVO.SelectionInfo selection;

    @Schema(description = "规格维度定义")
    private List<ProductDetailRespVO.SpecGroupInfo> specGroups;

    @Schema(description = "SKU列表")
    private List<SkuItem> skuList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "选择器SKU项")
    public static class SkuItem {

        @Schema(description = "SKU ID")
        private Long skuId;

        @Schema(description = "规格值")
        private List<ProductDetailRespVO.AttrValueInfo> specValues;

        @Schema(description = "库存信息")
        private StockInfo stock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "选择器库存信息")
    public static class StockInfo {

        @Schema(description = "是否有货")
        private Boolean inStock;

        @Schema(description = "库存状态: 0-无货, 1-有货")
        private Integer stockStatus;

        @Schema(description = "是否低库存")
        private Boolean lowStock;
    }
}
