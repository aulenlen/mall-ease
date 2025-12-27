package com.mallease.search.dto.converter;

import com.mallease.search.document.SpuDocument;
import com.mallease.search.dto.vo.SpuSearchResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpuDocConverter {
    // SpuDocument -> SpuSearchResultVO
    @Mapping(target = "isNew", expression = "java(spu.getNewStatus() != null && spu.getNewStatus() == 1)")
    @Mapping(target = "highlightName", ignore = true)
    @Mapping(target = "score", ignore = true)
    SpuSearchResultVO docToVo(SpuDocument spu);

}
