package com.mallease.app.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * App 首页聚合数据 VO
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "App首页数据")
public class HomePageVO {

    @Schema(description = "轮播图列表")
    private List<HomeBannerVO> banners;

    @Schema(description = "金刚区分类")
    private List<HomeCategoryVO> navCategories;

    @Schema(description = "当前秒杀数据")
    private HomeFlashVO flashData;

    @Schema(description = "编辑精选列表")
    private List<HomeEditorialVO> editorials;

    @Schema(description = "推荐商品列表")
    private List<HomeRecommendVO> recommendProducts;
}