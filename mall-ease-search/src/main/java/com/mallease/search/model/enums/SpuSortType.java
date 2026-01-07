package com.mallease.search.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;

/**
 * 商品搜索排序类型枚举
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Getter
@AllArgsConstructor
public enum SpuSortType {
    /**
     * 综合排序（默认）
     * 无关键词：sort DESC, sale DESC, updateTime DESC, spuId DESC
     * 有关键词：_score DESC, sort DESC, sale DESC, spuId DESC
     */
    COMPREHENSIVE(0, "综合"),

    /**
     * 销量排序
     * sale DESC, sort DESC, spuId DESC
     */
    SALE(1, "销量"),

    /**
     * 价格升序
     * minPrice ASC, sort DESC, spuId DESC
     */
    PRICE_ASC(2, "价格升序"),

    /**
     * 价格降序
     * minPrice DESC, sort DESC, spuId DESC
     */
    PRICE_DESC(3, "价格降序"),

    /**
     * 新品排序
     * newStatus DESC, updateTime DESC, spuId DESC
     */
    NEW_PRODUCT(4, "新品");

    private final int code;
    private final String desc;

    /**
     * 根据 code 获取枚举，无效值返回 COMPREHENSIVE
     */
    public static SpuSortType fromCode(Integer code) {
        if (code == null) {
            return COMPREHENSIVE;
        }
        for (SpuSortType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return COMPREHENSIVE;
    }

    /**
     * 构建排序对象
     *
     * @param hasKeyword 是否有搜索关键词
     * @return Spring Data Sort 对象
     */
    public Sort buildSort(boolean hasKeyword) {
        return switch (this) {
            case COMPREHENSIVE -> hasKeyword
                    ? Sort.by(
                    Sort.Order.desc("_score"),
                    Sort.Order.desc("sort"),
                    Sort.Order.desc("sale"),
                    Sort.Order.desc("spuId"))
                    : Sort.by(
                    Sort.Order.desc("sort"),
                    Sort.Order.desc("sale"),
                    Sort.Order.desc("updateTime"),
                    Sort.Order.desc("spuId"));

            case SALE -> Sort.by(
                    Sort.Order.desc("sale"),
                    Sort.Order.desc("sort"),
                    Sort.Order.desc("spuId"));

            case PRICE_ASC -> Sort.by(
                    Sort.Order.asc("minPrice"),
                    Sort.Order.desc("sort"),
                    Sort.Order.desc("spuId"));

            case PRICE_DESC -> Sort.by(
                    Sort.Order.desc("minPrice"),
                    Sort.Order.desc("sort"),
                    Sort.Order.desc("spuId"));

            case NEW_PRODUCT -> Sort.by(
                    Sort.Order.desc("newStatus"),
                    Sort.Order.desc("updateTime"),
                    Sort.Order.desc("spuId"));
        };
    }
}
