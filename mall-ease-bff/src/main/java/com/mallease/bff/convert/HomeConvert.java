package com.mallease.bff.convert;

import com.mallease.bff.controller.portal.common.vo.RecommendProductRespVO;
import com.mallease.bff.controller.portal.home.vo.HomeArticleRespVO;
import com.mallease.bff.controller.portal.home.vo.HomeBannerRespVO;
import com.mallease.bff.controller.portal.home.vo.HomeCategoryRespVO;
import com.mallease.bff.controller.portal.home.vo.HomeFlashRespVO;
import com.mallease.common.dto.remote.ArticleDTO;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.SlotCardDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 首页数据转换器
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Mapper(componentModel = "spring")
public interface HomeConvert {

    @Mapping(target = "type", source = "jumpType")
    @Mapping(target = "targetId", source = "jumpTargetId")
    HomeBannerRespVO toHomeBannerResp(SlotCardDTO dto);

    List<HomeBannerRespVO> toHomeBannerRespList(List<SlotCardDTO> dtoList);

    HomeCategoryRespVO toHomeCategoryResp(CategoryDTO dto);

    List<HomeCategoryRespVO> toHomeCategoryRespList(List<CategoryDTO> dtoList);

    HomeArticleRespVO toHomeArticleResp(ArticleDTO dto);

    List<HomeArticleRespVO> toHomeArticleRespList(List<ArticleDTO> dtoList);

    RecommendProductRespVO toRecommendProductResp(SpuRecommendDTO dto);

    List<RecommendProductRespVO> toRecommendProductRespList(List<SpuRecommendDTO> dtoList);

    HomeFlashRespVO toHomeFlashResp(FlashCurrentDTO dto);

    HomeFlashRespVO.FlashProductRespVO toHomeFlashProductResp(FlashCurrentDTO.FlashProduct dto);

    List<HomeFlashRespVO.FlashProductRespVO> toHomeFlashProductRespList(List<FlashCurrentDTO.FlashProduct> dtoList);
}
