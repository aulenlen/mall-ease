package com.mallease.bff.controller;

import com.mallease.bff.converter.HomeConverter;
import com.mallease.bff.feign.ContentFeignClient;
import com.mallease.bff.feign.MarketingFeignClient;
import com.mallease.bff.feign.ProductFeignClient;
import com.mallease.bff.feign.SearchFeignClient;
import com.mallease.bff.model.client.vo.HomePageVO;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * BFF 首页控制器
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Tag(name = "BFF首页", description = "首页聚合数据接口")
@RestController
@RequestMapping("/bff/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final ContentFeignClient contentFeignClient;
    private final ProductFeignClient productFeignClient;
    private final MarketingFeignClient marketingFeignClient;
    private final SearchFeignClient searchFeignClient;
    private final HomeConverter homeConverter;

    @Operation(summary = "获取首页数据", description = "聚合返回Banner、分类、秒杀、编辑精选、推荐商品")
    @GetMapping("/index")
    public R<HomePageVO> getHomeData() {
        List<BannerDTO> banners = safeGetData(() -> contentFeignClient.listPublishedBanners("home"));
        List<CategoryDTO> categories = safeGetData(productFeignClient::listNavCategories);
        FlashCurrentDTO flashData = safeGetSingleData(marketingFeignClient::getCurrentFlashData);
        List<EditorialDTO> editorials = safeGetData(() -> contentFeignClient.listPublishedEditorials(10));
        List<SpuRecommendDTO> recommendProducts = safeGetData(() -> searchFeignClient.listRecommend(20));

        HomePageVO homePageVO = HomePageVO.builder()
                .banners(homeConverter.bannerDTOListToVOList(banners))
                .navCategories(homeConverter.categoryDTOListToVOList(categories))
                .flashData(homeConverter.flashDTOToVO(flashData))
                .editorials(homeConverter.editorialDTOListToVOList(editorials))
                .recommendProducts(homeConverter.spuRecommendDTOListToVOList(recommendProducts))
                .build();

        return R.success(homePageVO);
    }

    /**
     * 安全获取列表数据（带降级）
     */
    private <T> List<T> safeGetData(java.util.function.Supplier<R<List<T>>> supplier) {
        try {
            R<List<T>> result = supplier.get();
            if (result != null && result.getData() != null) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("获取首页数据失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 安全获取单个数据（带降级）
     */
    private <T> T safeGetSingleData(java.util.function.Supplier<R<T>> supplier) {
        try {
            R<T> result = supplier.get();
            if (result != null) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("获取首页数据失败", e);
        }
        return null;
    }
}