package com.mallease.bff.service.home;

import com.mallease.bff.controller.portal.home.vo.HomePageRespVO;
import com.mallease.bff.convert.HomeConvert;
import com.mallease.bff.feign.content.ContentFeignClient;
import com.mallease.bff.feign.marketing.MarketingFeignClient;
import com.mallease.bff.feign.product.ProductFeignClient;
import com.mallease.bff.feign.search.SearchFeignClient;
import com.mallease.bff.service.support.RemoteCallSupport;
import com.mallease.common.dto.remote.BannerDTO;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.EditorialDTO;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页聚合服务实现。
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final ContentFeignClient contentFeignClient;
    private final ProductFeignClient productFeignClient;
    private final MarketingFeignClient marketingFeignClient;
    private final SearchFeignClient searchFeignClient;
    private final HomeConvert homeConvert;
    private final RemoteCallSupport remoteCallSupport;

    @Override
    public HomePageRespVO getHomePage() {
        List<BannerDTO> bannerDTOList = remoteCallSupport.getList(() -> contentFeignClient.listPublishedBanners("home"), "首页轮播图");
        List<CategoryDTO> categoryDTOList = remoteCallSupport.getList(productFeignClient::listNavCategories, "首页分类");
        FlashCurrentDTO flashCurrentDTO = remoteCallSupport.getOne(marketingFeignClient::getCurrentFlashData, "首页秒杀");
        List<EditorialDTO> editorialDTOList = remoteCallSupport.getList(() -> contentFeignClient.listPublishedEditorials(10), "首页编辑精选");
        List<SpuRecommendDTO> recommendDTOList = remoteCallSupport.getList(() -> searchFeignClient.listRecommend(20), "首页推荐商品");

        return HomePageRespVO.builder()
                .banners(homeConvert.toHomeBannerRespList(bannerDTOList))
                .navCategories(homeConvert.toHomeCategoryRespList(categoryDTOList))
                .flashData(homeConvert.toHomeFlashResp(flashCurrentDTO))
                .editorials(homeConvert.toHomeEditorialRespList(editorialDTOList))
                .recommendProducts(homeConvert.toRecommendProductRespList(recommendDTOList))
                .build();
    }
}