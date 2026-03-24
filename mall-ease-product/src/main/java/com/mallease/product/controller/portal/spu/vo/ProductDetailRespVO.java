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
public class ProductDetailRespVO {

    @Schema(description = "SPU基础信息")
    private SpuBasicInfo spuBasic;

    @Schema(description = "SPU详情信息")
    private SpuDetailInfo spuDetail;

    @Schema(description = "品牌信息")
    private BrandInfo brand;

    @Schema(description = "分类信息")
    private CategoryInfo category;

    @Schema(description = "SKU列表")
    private List<SkuInfo> skuList;

    @Schema(description = "缓存生成时间戳")
    private Long cacheTime;

    @Schema(description = "数据版本号")
    private Integer version;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SPU基础信息")
    public static class SpuBasicInfo {

        @Schema(description = "SPU ID")
        private Long id;

        @Schema(description = "SPU编码")
        private String spuCode;

        @Schema(description = "SPU名称")
        private String name;

        @Schema(description = "副标题")
        private String subTitle;

        @Schema(description = "SPU描述")
        private String description;

        @Schema(description = "关键字")
        private String keywords;

        @Schema(description = "SPU主图URL")
        private String pic;

        @Schema(description = "画册图片列表")
        private List<String> albumPicList;

        @Schema(description = "单位")
        private String unit;

        @Schema(description = "商品重量（克）")
        private BigDecimal weight;

        @Schema(description = "上架状态: 0-下架, 1-上架")
        private Integer publishStatus;

        @Schema(description = "新品状态: 0-不是新品, 1-新品")
        private Integer newStatus;

        @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
        private Integer recommendStatus;

        @Schema(description = "排序")
        private Integer sort;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SPU详情信息")
    public static class SpuDetailInfo {

        @Schema(description = "详情标题")
        private String detailTitle;

        @Schema(description = "详情描述（摘要）")
        private String detailDesc;

        @Schema(description = "产品服务列表")
        private List<String> serviceList;

        @Schema(description = "总销量")
        private Integer totalSale;

        @Schema(description = "最低价格")
        private BigDecimal minPrice;

        @Schema(description = "最高价格")
        private BigDecimal maxPrice;

        @Schema(description = "是否有货")
        private Boolean inStock;
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
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU聚合信息")
    public static class SkuInfo {

        @Schema(description = "SKU基础信息")
        private SkuBasicInfo basic;

        @Schema(description = "SKU价格信息")
        private SkuPriceInfo price;

        @Schema(description = "SKU配置信息")
        private SkuConfigInfo config;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU基础信息")
    public static class SkuBasicInfo {

        @Schema(description = "SKU ID")
        private Long id;

        @Schema(description = "SKU编码")
        private String skuCode;

        @Schema(description = "SKU属性值（JSON格式）")
        private String attrValues;

        @Schema(description = "SKU图片URL")
        private String pic;

        @Schema(description = "启用状态: 0-禁用, 1-启用")
        private Integer enableStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU价格信息")
    public static class SkuPriceInfo {

        @Schema(description = "SKU基础成交价")
        private BigDecimal basePrice;

        @Schema(description = "SKU划线参考价")
        private BigDecimal compareAtPrice;

        @Schema(description = "SKU活动价")
        private BigDecimal promotionPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU配置信息")
    public static class SkuConfigInfo {

        @Schema(description = "库存预警值")
        private Integer lowStock;
    }

    public Long getId() {
        return spuBasic != null ? spuBasic.getId() : null;
    }

    public String getName() {
        return spuBasic != null ? spuBasic.getName() : null;
    }
}
