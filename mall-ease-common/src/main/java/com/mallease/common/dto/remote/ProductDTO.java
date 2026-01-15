package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private SpuBasicInfo spuBasic;

    private SpuDetailInfo spuDetail;

    private BrandInfo brand;

    private CategoryInfo category;

    private List<SkuInfo> skuList;

    private List<FullReductionInfo> fullReductionList;

    private Long cacheTime;

    private Integer version;

    /**
     * SPU基础信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpuBasicInfo {

        private Long id;

        private String spuCode;

        private String name;

        private String subTitle;

        private String description;

        private String keywords;

        private String pic;

        private List<String> albumPicList;

        private String unit;

        private BigDecimal weight;

        private Integer publishStatus;

        private Integer newStatus;

        private Integer recommendStatus;

        private Integer sort;
    }

    /**
     * SPU详情信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpuDetailInfo {

        private String detailTitle;

        private String detailDesc;

        private List<String> serviceList;

        private Long freightTemplateId;

        private Integer totalSale;

        private BigDecimal minPrice;

        private BigDecimal maxPrice;

        private Integer totalStock;
    }

    /**
     * 品牌信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandInfo {

        private Long id;

        private String name;

        private String logo;
    }

    /**
     * 分类信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {

        private Long id;

        private String name;

        private String categoryIds;
    }

    /**
     * SKU聚合信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuInfo {

        private SkuBasicInfo basic;

        private SkuPriceInfo price;

        private SkuPromotionInfo promotion;

        private SkuBenefitInfo benefit;

        private SkuConfigInfo config;
    }

    /**
     * SKU基础信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuBasicInfo {

        private Long id;

        private String skuCode;

        private String attrValues;

        private String pic;

        private Integer enableStatus;
    }

    /**
     * SKU价格信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuPriceInfo {

        private BigDecimal price;

        private BigDecimal originalPrice;
    }

    /**
     * SKU促销信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuPromotionInfo {

        private Integer type;

        private BigDecimal price;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        private Integer perLimit;
    }

    /**
     * SKU会员权益
     * <p>低频变化（会员策略调整）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuBenefitInfo {

        private Integer giftPoint;

        private Integer giftGrowth;
    }

    /**
     * SKU配置信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuConfigInfo {

        private Integer lowStock;
    }

    /**
     * 满减规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FullReductionInfo {

        private BigDecimal fullPrice;

        private BigDecimal reducePrice;
    }

    /**
     * 获取SPU ID
     */
    public Long getId() {
        return spuBasic != null ? spuBasic.getId() : null;
    }

    /**
     * 获取SPU名称
     */
    public String getName() {
        return spuBasic != null ? spuBasic.getName() : null;
    }
}

