package com.mallease.bff.convert;

import com.mallease.bff.controller.portal.common.vo.RecommendProductRespVO;
import com.mallease.bff.controller.portal.home.vo.HomeBannerRespVO;
import com.mallease.bff.controller.portal.home.vo.HomeCategoryRespVO;
import com.mallease.bff.controller.portal.home.vo.HomeEditorialRespVO;
import com.mallease.bff.controller.portal.home.vo.HomeFlashRespVO;
import com.mallease.common.dto.remote.BannerDTO;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.EditorialDTO;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 首页数据转换器
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Mapper(componentModel = "spring")
public interface HomeConvert {

    HomeBannerRespVO toHomeBannerResp(BannerDTO dto);

    List<HomeBannerRespVO> toHomeBannerRespList(List<BannerDTO> dtoList);

    HomeCategoryRespVO toHomeCategoryResp(CategoryDTO dto);

    List<HomeCategoryRespVO> toHomeCategoryRespList(List<CategoryDTO> dtoList);

    HomeEditorialRespVO toHomeEditorialResp(EditorialDTO dto);

    List<HomeEditorialRespVO> toHomeEditorialRespList(List<EditorialDTO> dtoList);

    RecommendProductRespVO toRecommendProductResp(SpuRecommendDTO dto);

    List<RecommendProductRespVO> toRecommendProductRespList(List<SpuRecommendDTO> dtoList);

    HomeFlashRespVO toHomeFlashResp(FlashCurrentDTO dto);

    HomeFlashRespVO.FlashProductRespVO toHomeFlashProductResp(FlashCurrentDTO.FlashProduct dto);

    List<HomeFlashRespVO.FlashProductRespVO> toHomeFlashProductRespList(List<FlashCurrentDTO.FlashProduct> dtoList);
}
