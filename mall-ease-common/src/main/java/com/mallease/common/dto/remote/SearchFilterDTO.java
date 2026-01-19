package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 搜索聚合筛选项（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilterDTO {

    /**
     * 品牌筛选项
     */
    private List<FilterItem> brands;

    /**
     * 分类筛选项
     */
    private List<FilterItem> categories;

    /**
     * 属性筛选项
     */
    private List<AttrFilterItem> attrs;

    /**
     * 价格区间
     */
    private PriceRange priceRange;

    /**
     * 筛选项（品牌/分类）
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FilterItem {
        private Long id;
        private String name;
        private Long count;
    }

    /**
     * 属性筛选项
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttrFilterItem {
        private Long attrId;
        private String attrName;
        private List<AttrValue> values;
    }

    /**
     * 属性值
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttrValue {
        private String value;
        private Long count;
    }

    /**
     * 价格区间
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceRange {
        private BigDecimal min;
        private BigDecimal max;
    }
}