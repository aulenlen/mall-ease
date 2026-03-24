package com.mallease.product.convert.category;

import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.product.controller.admin.category.vo.CategoryDetailRespVO;
import com.mallease.product.controller.admin.category.vo.CategoryListRespVO;
import com.mallease.product.controller.admin.category.vo.CategorySaveReqVO;
import com.mallease.product.controller.admin.category.vo.CategoryTreeRespVO;
import com.mallease.product.dal.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类转换器
 */
@Mapper(componentModel = "spring")
public interface CategoryConvert {

    CategoryListRespVO entityToListRespVO(Category entity);

    CategoryDetailRespVO entityToDetailRespVO(Category entity);

    CategoryTreeRespVO entityToTreeRespVO(Category entity);

    List<CategoryTreeRespVO> entityListToTreeRespVOList(List<Category> entities);

    List<CategoryListRespVO> entityListToListRespVOList(List<Category> entities);

    CategoryDTO entityToDTO(Category entity);

    List<CategoryDTO> entityListToDTOList(List<Category> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    Category reqVOToEntity(CategorySaveReqVO reqVO);

    default List<CategoryTreeRespVO> buildTree(List<Category> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<CategoryTreeRespVO> treeRespVOList = entityListToTreeRespVOList(entities);
        Map<Long, CategoryTreeRespVO> idMap = treeRespVOList.stream()
                .collect(Collectors.toMap(CategoryTreeRespVO::getId, respVO -> respVO));

        List<CategoryTreeRespVO> roots = new ArrayList<>();
        for (CategoryTreeRespVO respVO : treeRespVOList) {
            Long parentId = respVO.getParentId();
            if (parentId == null || parentId == 0L) {
                roots.add(respVO);
            } else {
                CategoryTreeRespVO parent = idMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(respVO);
                } else {
                    roots.add(respVO);
                }
            }
        }
        return roots;
    }

    CategoryTreeDTO entityToTreeDTO(Category entity);

    List<CategoryTreeDTO> entityListToTreeDTOList(List<Category> entities);

    default List<CategoryTreeDTO> buildTreeDTO(List<Category> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<CategoryTreeDTO> treeDtoList = entityListToTreeDTOList(entities);
        Map<Long, CategoryTreeDTO> idMap = treeDtoList.stream()
                .collect(Collectors.toMap(CategoryTreeDTO::getId, dto -> dto));

        List<CategoryTreeDTO> roots = new ArrayList<>();
        for (CategoryTreeDTO dto : treeDtoList) {
            Long parentId = dto.getParentId();
            if (parentId == null || parentId == 0L) {
                roots.add(dto);
            } else {
                CategoryTreeDTO parent = idMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dto);
                } else {
                    roots.add(dto);
                }
            }
        }
        return roots;
    }
}