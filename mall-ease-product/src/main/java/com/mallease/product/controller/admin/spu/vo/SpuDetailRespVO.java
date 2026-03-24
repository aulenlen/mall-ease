package com.mallease.product.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台商品详情返回
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "后台商品详情返回")
public class SpuDetailRespVO {

    @Schema(description = "SPU ID")
    private Long id;

    @Schema(description = "SPU编码")
    private String spuCode;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类路径")
    private String categoryIds;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "关键字")
    private String keywords;

    @Schema(description = "主图")
    private String pic;

    @Schema(description = "画册图")
    private List<String> albumPics;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "重量")
    private BigDecimal weight;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer publishStatus;

    @Schema(description = "新品状态: 0-不是新品, 1-新品")
    private Integer newStatus;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    private Integer recommendStatus;

    @Schema(description = "审核状态: 0-未审核, 1-审核通过")
    private Integer verifyStatus;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "销量")
    private Integer sale;

    @Schema(description = "最低价")
    private BigDecimal minPrice;

    @Schema(description = "最高价")
    private BigDecimal maxPrice;

    @Schema(description = "是否有货")
    private Boolean inStock;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "商品详情扩展")
    private SpuDetailData spuDetail;

    @Schema(description = "SPU参数")
    private List<AttrValueRespVO> attrValueList;

    @Schema(description = "SKU列表")
    private List<SkuRespVO> skuList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "商品详情扩展")
    public static class SpuDetailData {

        @Schema(description = "详情标题")
        private String detailTitle;

        @Schema(description = "详情描述")
        private String detailDesc;

        @Schema(description = "PC详情HTML")
        private String detailHtml;

        @Schema(description = "移动端详情HTML")
        private String detailMobileHtml;

        @Schema(description = "服务ID列表（逗号分隔）")
        private String serviceIds;

        @Schema(description = "包装清单")
        private String packingList;

        @Schema(description = "售后服务")
        private String afterSaleService;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "属性值")
    public static class AttrValueRespVO {

        @Schema(description = "属性ID")
        private Long attrId;

        @Schema(description = "属性名称")
        private String attrName;

        @Schema(description = "属性值")
        private String attrValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU详情")
    public static class SkuRespVO {

        @Schema(description = "SKU ID")
        private Long id;

        @Schema(description = "SKU编码")
        private String skuCode;

        @Schema(description = "SKU图片")
        private String pic;

        @Schema(description = "SKU基础成交价")
        private BigDecimal basePrice;

        @Schema(description = "SKU划线参考价")
        private BigDecimal compareAtPrice;

        @Schema(description = "启用状态: 0-禁用, 1-启用")
        private Integer enableStatus;

        @Schema(description = "SKU规格")
        private List<AttrValueRespVO> attrValues;
    }
}
