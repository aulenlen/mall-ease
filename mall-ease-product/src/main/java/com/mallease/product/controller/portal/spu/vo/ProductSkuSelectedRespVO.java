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
@Schema(description = "前台SKU成交上下文响应")
public class ProductSkuSelectedRespVO {

    @Schema(description = "SKU信息")
    private ProductDetailRespVO.SkuInfo sku;

    @Schema(description = "规格值")
    private List<ProductDetailRespVO.AttrValueInfo> specValues;

    @Schema(description = "库存信息")
    private ProductSelectorRespVO.StockInfo stock;
}
