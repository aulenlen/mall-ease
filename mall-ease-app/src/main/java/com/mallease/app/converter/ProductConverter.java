package com.mallease.app.converter;

import com.mallease.app.model.vo.ProductVO;
import com.mallease.common.dto.remote.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductConverter {
    ProductVO dtoToVo(ProductDTO dto);
}
