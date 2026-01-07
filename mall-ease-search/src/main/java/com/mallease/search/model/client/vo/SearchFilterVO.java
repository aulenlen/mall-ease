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

    @Schema(description = "规格聚合")
    private List<SpecAggVO> specs;

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
    @Schema(description = "规格聚合项")
    public static class SpecAggVO {
        private Long specId;
        private String specName;
        private List<SpecValueAggVO> values;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "规格值聚合项")
    public static class SpecValueAggVO {
        private String value;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "价格区间聚合项")
    public static class PriceRangeVO {
        private String label;
        private BigDecimal from;
        private BigDecimal to;
        private Long count;
    }
}
