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
@Schema(description = "前台秒杀商品详情响应")
public class FlashPortalDetailRespVO {

    @Schema(description = "秒杀场次ID")
    private Long sessionId;

    @Schema(description = "SPU静态信息")
    private ProductDTO.SpuInfo spu;

    @Schema(description = "SPU详情扩展")
    private ProductDTO.SpuDetailInfo spuDetail;

    @Schema(description = "SPU销售摘要")
    private ProductDTO.SpuSaleInfo sale;

    @Schema(description = "SPU库存摘要")
    private ProductDTO.SpuStockInfo stock;

    @Schema(description = "品牌信息")
    private ProductDTO.BrandInfo brand;

    @Schema(description = "分类信息")
    private ProductDTO.CategoryInfo category;

    @Schema(description = "商品参数")
    private List<ProductDTO.AttrValueInfo> params;

    @Schema(description = "默认选中信息")
    private ProductDTO.SelectionInfo selection;

    @Schema(description = "规格维度定义")
    private List<ProductDTO.SpecGroupInfo> specGroups;

    @Schema(description = "SKU列表")
    private List<ProductDTO.SkuViewInfo> skuList;

    @Schema(description = "当前选中SKU")
    private ProductDTO.SkuViewInfo currentSku;
}
