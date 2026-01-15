package com.mallease.product.model.data.cache;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SPU缓存聚合
 * @author: Aulen
 * @create: 2025-12-22
 */
@Schema(description = "SPU缓存聚合根")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuCache implements Serializable {

    private static final long serialVersionUID = 1L;

    // SPU 聚合

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

    @Schema(description = "满减规则列表")
    private List<FullReductionInfo> fullReductionList;

    // 缓存元数据

    @Schema(description = "缓存生成时间戳")
    private Long cacheTime;

    @Schema(description = "数据版本号")
    private Integer version;

    // SPU 基础信息

    /**
     * SPU基础信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SPU基础信息")
    public static class SpuBasicInfo implements Serializable {

        private static final long serialVersionUID = 1L;

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

    //  SPU 详情信息

    /**
     * SPU详情信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SPU详情信息")
    public static class SpuDetailInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "详情标题")
        private String detailTitle;

        @Schema(description = "详情描述（摘要）")
        private String detailDesc;

        @Schema(description = "产品服务列表")
        private List<String> serviceList;

        @Schema(description = "运费模板ID")
        private Long freightTemplateId;

        @Schema(description = "总销量")
        private Integer totalSale;

        @Schema(description = "最低价格")
        private BigDecimal minPrice;

        @Schema(description = "最高价格")
        private BigDecimal maxPrice;

        @Schema(description = "总库存（快照值）")
        private Integer totalStock;
    }

    // 品牌信息

    /**
     * 品牌信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "品牌信息")
    public static class BrandInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "品牌ID")
        private Long id;

        @Schema(description = "品牌名称")
        private String name;

        @Schema(description = "品牌Logo")
        private String logo;
    }

    //  分类信息

    /**
     * 分类信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类信息")
    public static class CategoryInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "分类ID")
        private Long id;

        @Schema(description = "分类名称")
        private String name;

        @Schema(description = "分类路径")
        private String categoryIds;
    }

    //  SKU 聚合信息

    /**
     * SKU聚合信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU聚合信息")
    public static class SkuInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "SKU基础信息")
        private SkuBasicInfo basic;

        @Schema(description = "SKU价格信息")
        private SkuPriceInfo price;

        @Schema(description = "SKU促销信息")
        private SkuPromotionInfo promotion;

        @Schema(description = "SKU会员权益")
        private SkuBenefitInfo benefit;

        @Schema(description = "SKU配置信息")
        private SkuConfigInfo config;
    }

    // SKU 基础信息

    /**
     * SKU基础信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU基础信息")
    public static class SkuBasicInfo implements Serializable {

        private static final long serialVersionUID = 1L;

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

    // SKU 价格信息

    /**
     * SKU价格信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU价格信息")
    public static class SkuPriceInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "SKU售价")
        private BigDecimal price;

        @Schema(description = "市场价")
        private BigDecimal originalPrice;
    }

    // SKU 促销信息

    /**
     * SKU促销信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU促销信息")
    public static class SkuPromotionInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "促销类型: 0-无, 1-促销价, 2-会员价, 3-阶梯价, 4-满减, 5-限时购")
        private Integer type;

        @Schema(description = "促销价格")
        private BigDecimal price;

        @Schema(description = "促销开始时间")
        private LocalDateTime startTime;

        @Schema(description = "促销结束时间")
        private LocalDateTime endTime;

        @Schema(description = "活动限购数量")
        private Integer perLimit;
    }

    // SKU 会员权益

    /**
     * SKU会员权益
     * <p>低频变化（会员策略调整）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU会员权益")
    public static class SkuBenefitInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "赠送积分")
        private Integer giftPoint;

        @Schema(description = "赠送成长值")
        private Integer giftGrowth;
    }

    // SKU 配置信息

    /**
     * SKU配置信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "SKU配置信息")
    public static class SkuConfigInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "库存预警值")
        private Integer lowStock;
    }

    // 满减规则

    /**
     * 满减规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "满减规则")
    public static class FullReductionInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "满足金额")
        private BigDecimal fullPrice;

        @Schema(description = "减少金额")
        private BigDecimal reducePrice;
    }

    // 辅助方法

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
