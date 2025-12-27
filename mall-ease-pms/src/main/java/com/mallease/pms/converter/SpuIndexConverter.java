package com.mallease.pms.converter;

import com.mallease.common.dto.SpuIndexDTO;
import com.mallease.pms.pojo.PmsSpu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpuIndexConverter {
    @Mapping(source = "id", target = "spuId")
    SpuIndexDTO spuEntityToDto(PmsSpu spu);

    List<SpuIndexDTO> spuEntityListToDtoList(List<PmsSpu> spuList);
}
