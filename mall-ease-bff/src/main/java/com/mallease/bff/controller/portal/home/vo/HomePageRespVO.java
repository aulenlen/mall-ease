package com.mallease.bff.controller.portal.home.vo;

import com.mallease.bff.controller.portal.common.vo.RecommendProductRespVO;
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
public class HomePageRespVO {

    @Schema(description = "轮播图列表")
    private List<HomeBannerRespVO> banners;

    @Schema(description = "金刚区分类")
    private List<HomeCategoryRespVO> navCategories;

    @Schema(description = "当前秒杀数据")
    private HomeFlashRespVO flashData;

    @Schema(description = "编辑精选列表")
    private List<HomeEditorialRespVO> editorials;

    @Schema(description = "推荐商品列表")
    private List<RecommendProductRespVO> recommendProducts;
}
