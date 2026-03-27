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

    HomeBannerRespVO bannerDTOToRespVO(BannerDTO dto);

    List<HomeBannerRespVO> bannerDTOListToRespVOList(List<BannerDTO> dtoList);

    HomeCategoryRespVO categoryDTOToRespVO(CategoryDTO dto);

    List<HomeCategoryRespVO> categoryDTOListToRespVOList(List<CategoryDTO> dtoList);

    HomeEditorialRespVO editorialDTOToRespVO(EditorialDTO dto);

    List<HomeEditorialRespVO> editorialDTOListToRespVOList(List<EditorialDTO> dtoList);

    RecommendProductRespVO spuRecommendDTOToRespVO(SpuRecommendDTO dto);

    List<RecommendProductRespVO> spuRecommendDTOListToRespVOList(List<SpuRecommendDTO> dtoList);

    HomeFlashRespVO flashDTOToRespVO(FlashCurrentDTO dto);

    HomeFlashRespVO.FlashProductRespVO flashProductDTOToRespVO(FlashCurrentDTO.FlashProduct dto);

    List<HomeFlashRespVO.FlashProductRespVO> flashProductDTOListToRespVOList(List<FlashCurrentDTO.FlashProduct> dtoList);
}
