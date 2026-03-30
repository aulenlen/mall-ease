package com.mallease.product.controller.portal.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "前台商品详情页响应")
public class ProductDetailRespVO {

    @Schema(description = "SPU静态信息")
    private SpuInfo spu;

    @Schema(description = "SPU详情扩展")
    private SpuDetailInfo spuDetail;

    @Schema(description = "SPU销售摘要")
    private SpuSaleInfo sale;

    @Schema(description = "品牌信息")
    private BrandInfo brand;

    @Schema(description = "分类信息")
    private CategoryInfo category;

    @Schema(description = "商品参数")
    private List<AttrValueInfo> params;

    @Schema(description = "默认选中信息")
    private SelectionInfo selection;

    @Schema(description = "当前选中SKU信息")
    private SkuInfo currentSku;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SPU静态信息")
    public static class SpuInfo {

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

        @Schema(description = "分类名称")
        private String categoryName;

        @Schema(description = "分类路径")
        private String categoryIds;

        @Schema(description = "SPU名称")
        private String name;

        @Schema(description = "副标题")
        private String subTitle;

        @Schema(description = "商品描述")
        private String description;

        @Schema(description = "关键字")
        private String keywords;

        @Schema(description = "主图URL")
        private String pic;

        @Schema(description = "画册图片列表")
        private List<String> albumPics;

        @Schema(description = "单位")
        private String unit;

        @Schema(description = "重量")
        private BigDecimal weight;

        @Schema(description = "上架状态")
        private Integer publishStatus;

        @Schema(description = "新品状态")
        private Integer newStatus;

        @Schema(description = "推荐状态")
        private Integer recommendStatus;

        @Schema(description = "排序值")
        private Integer sort;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SPU详情扩展")
    public static class SpuDetailInfo {

        @Schema(description = "详情标题")
        private String detailTitle;

        @Schema(description = "详情描述")
        private String detailDesc;

        @Schema(description = "PC详情HTML")
        private String detailHtml;

        @Schema(description = "移动端详情HTML")
        private String detailMobileHtml;

        @Schema(description = "服务项")
        private List<ServiceInfo> services;

        @Schema(description = "包装清单")
        private String packingList;

        @Schema(description = "售后服务")
        private String afterSaleService;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SPU销售摘要")
    public static class SpuSaleInfo {

        @Schema(description = "总销量")
        private Integer totalSale;

        @Schema(description = "最低价格")
        private BigDecimal minPrice;

        @Schema(description = "最高价格")
        private BigDecimal maxPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "品牌信息")
    public static class BrandInfo {

        @Schema(description = "品牌ID")
        private Long id;

        @Schema(description = "品牌名称")
        private String name;

        @Schema(description = "品牌Logo")
        private String logo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类信息")
    public static class CategoryInfo {

        @Schema(description = "分类ID")
        private Long id;

        @Schema(description = "分类名称")
        private String name;

        @Schema(description = "分类路径")
        private String categoryIds;

        @Schema(description = "分类面包屑")
        private List<BreadcrumbItem> breadcrumb;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "默认选中信息")
    public static class SelectionInfo {

        @Schema(description = "默认选中SKU ID")
        private Long defaultSkuId;

        @Schema(description = "当前选中规格值")
        private List<AttrValueInfo> selectedSpecValues;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU基础信息")
    public static class SkuInfo {

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

        @Schema(description = "SKU活动价")
        private BigDecimal promotionPrice;

        @Schema(description = "SKU展示价")
        private BigDecimal displayPrice;

        @Schema(description = "启用状态")
        private Integer enableStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "规格/参数值")
    public static class AttrValueInfo {

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
    @Schema(description = "服务项")
    public static class ServiceInfo {

        @Schema(description = "服务编码")
        private Integer code;

        @Schema(description = "服务名称")
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "规格维度定义")
    public static class SpecGroupInfo {

        @Schema(description = "属性ID")
        private Long attrId;

        @Schema(description = "属性名称")
        private String attrName;

        @Schema(description = "排序值")
        private Integer sort;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "面包屑项")
    public static class BreadcrumbItem {

        @Schema(description = "分类ID")
        private Long id;

        @Schema(description = "分类名称")
        private String name;
    }

    public Long getId() {
        return spu != null ? spu.getId() : null;
    }

    public String getName() {
        return spu != null ? spu.getName() : null;
    }
}
