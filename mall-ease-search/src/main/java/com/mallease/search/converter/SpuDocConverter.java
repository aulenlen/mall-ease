package com.mallease.search.converter;

import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.search.model.data.doc.SpuDocument;
import com.mallease.search.model.client.vo.SpuItemVO;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;



import java.util.List;



@Mapper(componentModel = "spring")

public interface SpuDocConverter {

    // SpuDocument -> SpuSearchResultVO

    @Mapping(target = "isNew", expression = "java(spu.getNewStatus() != null && spu.getNewStatus() == 1)")

    @Mapping(target = "highlightName", ignore = true)

    @Mapping(target = "score", ignore = true)

    SpuItemVO docToVo(SpuDocument spu);



    List<SpuItemVO> docListToVoList(List<SpuDocument> spuList);

    // SpuDocument -> SpuRecommendDTO（内部调用）
    @Mapping(target = "isNew", expression = "java(spu.getNewStatus() != null && spu.getNewStatus() == 1)")
    SpuRecommendDTO docToDTO(SpuDocument spu);

    List<SpuRecommendDTO> docListToDTOList(List<SpuDocument> spuList);
}
