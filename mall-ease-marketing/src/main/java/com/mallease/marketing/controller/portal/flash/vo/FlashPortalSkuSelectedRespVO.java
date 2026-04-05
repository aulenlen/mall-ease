package com.mallease.marketing.controller.portal.flash.vo;

import com.mallease.common.dto.remote.ProductDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "前台秒杀SKU选中响应")
public class FlashPortalSkuSelectedRespVO {

    @Schema(description = "秒杀场次ID")
    private Long sessionId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "当前SKU")
    private ProductDTO.SkuInfo sku;

    @Schema(description = "规格值")
    private List<ProductDTO.AttrValueInfo> specValues;

    @Schema(description = "库存信息")
    private ProductDTO.SkuStockInfo stock;
}
