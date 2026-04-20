package com.mallease.bff.service.home;

import com.mallease.bff.controller.portal.home.vo.HomePageRespVO;
import com.mallease.bff.convert.HomeConvert;
import com.mallease.bff.feign.content.ContentFeignClient;
import com.mallease.bff.feign.marketing.MarketingFeignClient;
import com.mallease.bff.feign.product.ProductFeignClient;
import com.mallease.bff.feign.search.SearchFeignClient;
import com.mallease.bff.service.support.RemoteCallSupport;
import com.mallease.common.constant.SlotCodeConstants;
import com.mallease.common.dto.remote.ArticleDTO;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.SlotCardDTO;
import com.mallease.common.dto.remote.SlotRenderDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        SlotRenderDTO bannerRender = remoteCallSupport.getOne(
                () -> contentFeignClient.getSlotRender(SlotCodeConstants.HOME_TOP_SWIPER, 10),
                "首页轮播图"
        );
        List<CategoryDTO> categoryDTOList = remoteCallSupport.getList(productFeignClient::listNavCategories, "首页分类");
        FlashCurrentDTO flashCurrentDTO = remoteCallSupport.getOne(marketingFeignClient::getCurrentFlashData, "首页秒杀");
        SlotRenderDTO articleRender = remoteCallSupport.getOne(
                () -> contentFeignClient.getSlotRender(SlotCodeConstants.HOME_ARTICLE_FEED, 10),
                "首页文章"
        );
        List<SpuRecommendDTO> recommendDTOList = remoteCallSupport.getList(() -> searchFeignClient.listRecommend(20), "首页推荐商品");

        return HomePageRespVO.builder()
                .banners(homeConvert.toHomeBannerRespList(extractCards(bannerRender)))
                .navCategories(homeConvert.toHomeCategoryRespList(categoryDTOList))
                .flashData(homeConvert.toHomeFlashResp(flashCurrentDTO))
                .articles(homeConvert.toHomeArticleRespList(extractArticles(articleRender)))
                .recommendProducts(homeConvert.toRecommendProductRespList(recommendDTOList))
                .build();
    }

    private List<SlotCardDTO> extractCards(SlotRenderDTO slotRender) {
        if (slotRender == null || slotRender.getCards() == null) {
            return Collections.emptyList();
        }
        return slotRender.getCards();
    }

    private List<ArticleDTO> extractArticles(SlotRenderDTO slotRender) {
        if (slotRender == null || slotRender.getArticles() == null) {
            return Collections.emptyList();
        }
        return slotRender.getArticles();
    }
}
