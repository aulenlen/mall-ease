package com.mallease.common.dto.remote;

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
public class ProductDTO {

    private SpuInfo spu;

    private SpuDetailInfo spuDetail;

    private SpuSaleInfo sale;

    private SpuStockInfo stock;

    private BrandInfo brand;

    private CategoryInfo category;

    private List<AttrValueInfo> params;

    private SelectionInfo selection;

    private List<SpecGroupInfo> specGroups;

    private List<SkuViewInfo> skuList;

    private SkuViewInfo currentSku;

    private CacheMetaInfo cacheMeta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpuInfo {

        private Long id;
        private String spuCode;
        private Long brandId;
        private String brandName;
        private Long categoryId;
        private String categoryName;
        private String categoryIds;
        private String name;
        private String subTitle;
        private String description;
        private String keywords;
        private String pic;
        private List<String> albumPics;
        private String unit;
        private BigDecimal weight;
        private Integer publishStatus;
        private Integer newStatus;
        private Integer recommendStatus;
        private Integer sort;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpuDetailInfo {

        private String detailTitle;
        private String detailDesc;
        private String detailHtml;
        private String detailMobileHtml;
        private List<ServiceInfo> services;
        private String packingList;
        private String afterSaleService;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpuSaleInfo {

        private Integer totalSale;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpuStockInfo {

        private Boolean inStock;
        private Integer stockStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandInfo {

        private Long id;
        private String name;
        private String logo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {

        private Long id;
        private String name;
        private String categoryIds;
        private List<BreadcrumbItem> breadcrumb;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectionInfo {

        private Long defaultSkuId;
        private List<AttrValueInfo> selectedSpecValues;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecGroupInfo {

        private Long attrId;
        private String attrName;
        private Integer sort;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuViewInfo {

        private SkuInfo sku;
        private List<AttrValueInfo> specValues;
        private SkuStockInfo stock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuInfo {

        private Long id;
        private String skuCode;
        private String pic;
        private BigDecimal basePrice;
        private BigDecimal compareAtPrice;
        private BigDecimal promotionPrice;
        private BigDecimal displayPrice;
        private Integer enableStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuStockInfo {

        private Boolean inStock;
        private Integer stockStatus;
        private Boolean lowStock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttrValueInfo {

        private Long attrId;
        private String attrName;
        private String attrValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceInfo {

        private Integer code;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BreadcrumbItem {

        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheMetaInfo {

        private Long cacheTime;
        private Integer version;
    }

    public Long getId() {
        return spu != null ? spu.getId() : null;
    }

    public String getName() {
        return spu != null ? spu.getName() : null;
    }
}
