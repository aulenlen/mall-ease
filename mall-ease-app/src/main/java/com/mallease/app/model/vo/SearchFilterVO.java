package com.mallease.app.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 搜索聚合筛选项
 *
 * @author: Aulen
 * @create: 2026-01-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "搜索聚合筛选项")
public class SearchFilterVO {

    @Schema(description = "品牌筛选项")
    private List<FilterItem> brands;

    @Schema(description = "分类筛选项")
    private List<FilterItem> categories;

    @Schema(description = "属性筛选项")
    private List<AttrFilterItem> attrs;

    @Schema(description = "价格区间")
    private PriceRange priceRange;

    /**
     * 筛选项（品牌/分类）
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "筛选项")
    public static class FilterItem {
        @Schema(description = "ID")
        private Long id;

        @Schema(description = "名称")
        private String name;

        @Schema(description = "商品数量")
        private Long count;
    }

    /**
     * 属性筛选项
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "属性筛选项")
    public static class AttrFilterItem {
        @Schema(description = "属性ID")
        private Long attrId;

        @Schema(description = "属性名称")
        private String attrName;

        @Schema(description = "属性值列表")
        private List<AttrValue> values;
    }

    /**
     * 属性值
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "属性值")
    public static class AttrValue {
        @Schema(description = "属性值")
        private String value;

        @Schema(description = "商品数量")
        private Long count;
    }

    /**
     * 价格区间
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "价格区间")
    public static class PriceRange {
        @Schema(description = "最低价格")
        private BigDecimal min;

        @Schema(description = "最高价格")
        private BigDecimal max;
    }
}