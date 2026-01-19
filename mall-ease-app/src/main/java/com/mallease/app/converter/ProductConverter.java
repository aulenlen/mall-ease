package com.mallease.app.converter;

import com.mallease.app.model.vo.ProductItemVO;
import com.mallease.app.model.vo.ProductSearchResultVO;
import com.mallease.app.model.vo.ProductVO;
import com.mallease.app.model.vo.SearchFilterVO;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SearchFilterDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductConverter {
    ProductVO dtoToVo(ProductDTO dto);

    /**
     * 商品搜索结果转换
     */
    ProductSearchResultVO searchResultDtoToVo(SpuSearchResultDTO dto);

    /**
     * 商品列表项转换
     */
    ProductItemVO recommendDtoToItemVo(SpuRecommendDTO dto);

    /**
     * 搜索筛选项转换
     */
    SearchFilterVO filterDtoToVo(SearchFilterDTO dto);
}
