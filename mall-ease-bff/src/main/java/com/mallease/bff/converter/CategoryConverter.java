package com.mallease.bff.converter;

import com.mallease.bff.model.client.vo.CategoryTreeVO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 首页数据转换器
 *
 * @author: Aulen
 * @create: 2026-01-09
 */
@Mapper(componentModel = "spring")
public interface CategoryConverter {
    CategoryTreeVO categoryTreeDTOToVO(CategoryTreeDTO dto);

    List<CategoryTreeVO> categoryTreeListDTOToVOList(List<CategoryTreeDTO> treeList);
}
