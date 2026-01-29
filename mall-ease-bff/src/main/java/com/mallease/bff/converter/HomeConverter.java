package com.mallease.bff.converter;

import com.mallease.bff.model.vo.*;
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
public interface HomeConverter {

    HomeBannerVO bannerDTOToVO(BannerDTO dto);

    List<HomeBannerVO> bannerDTOListToVOList(List<BannerDTO> dtoList);

    HomeCategoryVO categoryDTOToVO(CategoryDTO dto);

    List<HomeCategoryVO> categoryDTOListToVOList(List<CategoryDTO> dtoList);

    HomeEditorialVO editorialDTOToVO(EditorialDTO dto);

    List<HomeEditorialVO> editorialDTOListToVOList(List<EditorialDTO> dtoList);

    HomeRecommendVO spuRecommendDTOToVO(SpuRecommendDTO dto);

    List<HomeRecommendVO> spuRecommendDTOListToVOList(List<SpuRecommendDTO> dtoList);

    HomeFlashVO flashDTOToVO(FlashCurrentDTO dto);

    HomeFlashVO.FlashProductVO flashProductDTOToVO(FlashCurrentDTO.FlashProduct dto);

    List<HomeFlashVO.FlashProductVO> flashProductDTOListToVOList(List<FlashCurrentDTO.FlashProduct> dtoList);
}