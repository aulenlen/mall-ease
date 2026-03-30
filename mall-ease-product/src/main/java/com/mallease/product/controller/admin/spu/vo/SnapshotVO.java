package com.mallease.product.controller.admin.spu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotVO {
    private Spu spu;
    private Detail detail;
    private List<AttrValue> params;
    private List<SpecOption> specs;
    private List<Sku> skus;
    private Services services;
    private PublishMeta publishMeta;

    @Data
    public static class Spu {
        private Long id;
        private String spuCode;
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
        private Integer sale;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Boolean inStock;
        private Brand brand;
        private Category category;
    }

    @Data
    public static class Brand {
        private Long id;
        private String name;
    }

    @Data
    public static class Category {
        private Long id;
        private String name;
        private String path;
    }

    @Data
    public static class Detail {
        private String detailTitle;
        private String detailDesc;
        private String detailHtml;
        private String detailMobileHtml;
        private String packingList;
        private String afterSaleService;
    }

    @Data
    public static class AttrValue {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

    @Data
    public static class SpecOption {
        private Long attrId;
        private String attrName;
        private List<String> values;
    }

    @Data
    public static class Sku {
        private Long skuId;
        private String skuCode;
        private String name;
        private String pic;
        private BigDecimal basePrice;
        private BigDecimal compareAtPrice;
        private Integer enableStatus;
        private List<AttrValue> attrValues;
    }

    @Data
    public static class Services {
        private List<Integer> serviceIds;
    }

    @Data
    public static class PublishMeta {
        private Integer version;
        private LocalDateTime publishedAt;
        private String snapshotHash;
    }
}
