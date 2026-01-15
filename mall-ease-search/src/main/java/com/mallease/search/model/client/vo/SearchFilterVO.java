package com.mallease.search.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 筛选面板 VO（聚合结果）
 *
 * @author: Aulen
 * @create: 2025-12-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "筛选面板")
public class SearchFilterVO {

    @Schema(description = "品牌聚合")
    private List<BrandAggVO> brands;

    @Schema(description = "分类聚合")
    private List<CategoryAggVO> categories;

    @Schema(description = "属性聚合（可筛选的规格）")
    private List<AttrAggVO> attrs;

    @Schema(description = "价格区间聚合")
    private List<PriceRangeVO> priceRanges;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "品牌聚合项")
    public static class BrandAggVO {
        private Long brandId;
        private String brandName;
        @Schema(description = "品牌Logo")
        private String logo;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类聚合项")
    public static class CategoryAggVO {
        private Long categoryId;
        private String categoryName;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "属性聚合项")
    public static class AttrAggVO {
        private Long attrId;
        private String attrName;
        private List<AttrValueAggVO> values;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "属性值聚合项")
    public static class AttrValueAggVO {
        private String value;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "价格区间聚合项")
    public static class PriceRangeVO {
        @Schema(description = "区间标识，如 '0-300'")
        private String key;
        @Schema(description = "显示标签，如 '¥0-300'")
        private String label;
        private BigDecimal from;
        private BigDecimal to;
        private Long count;
    }
}
