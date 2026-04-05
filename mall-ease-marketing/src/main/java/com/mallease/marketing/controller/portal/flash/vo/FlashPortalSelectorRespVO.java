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
@Schema(description = "前台秒杀规格选择器响应")
public class FlashPortalSelectorRespVO {

    @Schema(description = "秒杀场次ID")
    private Long sessionId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "默认选中信息")
    private ProductDTO.SelectionInfo selection;

    @Schema(description = "规格维度定义")
    private List<ProductDTO.SpecGroupInfo> specGroups;

    @Schema(description = "SKU列表")
    private List<ProductDTO.SkuViewInfo> skuList;
}
