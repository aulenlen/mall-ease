package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 推荐商品传输对象（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpuRecommendDTO {

    private Long spuId;

    private String name;

    private String subTitle;

    private String pic;

    private Long brandId;

    private String brandName;

    private Long categoryId;

    private String categoryPath;

    private String categoryName;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer sale;

    private Boolean inStock;

    private Boolean isNew;
}