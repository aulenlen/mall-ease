package com.mallease.bff.convert;

import com.mallease.bff.controller.portal.category.vo.CategoryTreeRespVO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 首页数据转换器
 *
 * @author: Aulen
 * @create: 2026-01-09
 */
@Mapper(componentModel = "spring")
public interface CategoryConvert {
    @Mapping(target = "recommends", ignore = true)
    CategoryTreeRespVO categoryTreeDTOToRespVO(CategoryTreeDTO dto);

    List<CategoryTreeRespVO> categoryTreeListDTOToRespVOList(List<CategoryTreeDTO> treeList);
}
