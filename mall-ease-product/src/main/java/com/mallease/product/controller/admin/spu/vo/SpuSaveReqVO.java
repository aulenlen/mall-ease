package com.mallease.product.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "后台保存商品请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpuSaveReqVO {

    @Schema(description = "品牌ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200个字符")
    private String name;

    @Schema(description = "副标题")
    @Size(max = 255, message = "副标题长度不能超过255个字符")
    private String subTitle;

    @Schema(description = "商品描述")
    @NotBlank(message = "商品描述不能为空")
    @Size(max = 500, message = "商品描述长度不能超过500个字符")
    private String description;

    @Schema(description = "搜索关键词")
    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 255, message = "搜索关键词长度不能超过255个字符")
    private String keywords;

    @Schema(description = "主图")
    @Size(max = 255, message = "主图长度不能超过255个字符")
    private String pic;

    @Schema(description = "商品图集")
    @Size(max = 20, message = "商品图集不能超过20张")
    private List<@Size(max = 255, message = "图集地址长度不能超过255个字符") String> albumPics;

    @Schema(description = "单位")
    @Size(max = 16, message = "单位长度不能超过16个字符")
    private String unit;

    @Schema(description = "重量（克）")
    @DecimalMin(value = "0", message = "重量不能为负数")
    private BigDecimal weight;

    @Schema(description = "排序")
    @Min(value = 0, message = "排序值不能小于0")
    @Builder.Default
    private Integer sort = 0;

    @Schema(description = "详情信息")
    @Valid
    private SpuDetailReqVO spuDetail;

    @Schema(description = "商品参数")
    @Valid
    private List<@NotNull(message = "商品参数项不能为空") @Valid AttrValueReqVO> attrValueList;

    @Schema(description = "SKU列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "SKU列表不能为空")
    @Valid
    private List<@NotNull(message = "SKU项不能为空") @Valid SkuSaveReqVO> skuList;

    @Schema(description = "SPU详情请求")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SpuDetailReqVO {

        @Schema(description = "PC详情HTML")
        private String detailHtml;

        @Schema(description = "移动端详情HTML")
        private String detailMobileHtml;
    }

    @Schema(description = "商品参数")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttrValueReqVO {

        @Schema(description = "属性ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "属性ID不能为空")
        private Long attrId;

        @Schema(description = "属性名称", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "属性名称不能为空")
        @Size(max = 64, message = "属性名称长度不能超过64个字符")
        private String attrName;

        @Schema(description = "属性值", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "属性值不能为空")
        @Size(max = 256, message = "属性值长度不能超过256个字符")
        private String attrValue;
    }
}