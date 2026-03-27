package com.mallease.search.convert;

import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.search.dal.entity.EsSpu;
import com.mallease.search.controller.portal.search.vo.SpuItemRespVO;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;



import java.util.List;



@Mapper(componentModel = "spring")

public interface EsSpuConvert {

    // ES 实体 -> 搜索结果响应

    @Mapping(target = "isNew", expression = "java(spu.getNewStatus() != null && spu.getNewStatus() == 1)")

    @Mapping(target = "highlightName", ignore = true)

    @Mapping(target = "score", ignore = true)

    SpuItemRespVO entityToRespVO(EsSpu spu);



    List<SpuItemRespVO> entityListToRespVOList(List<EsSpu> spuList);

    // ES 实体 -> SpuRecommendDTO（内部调用）
    @Mapping(target = "isNew", expression = "java(spu.getNewStatus() != null && spu.getNewStatus() == 1)")
    SpuRecommendDTO entityToDTO(EsSpu spu);

    List<SpuRecommendDTO> entityListToDTOList(List<EsSpu> spuList);
}
