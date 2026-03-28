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

    CategoryListRespVO toCategoryListResp(Category entity);

    CategoryDetailRespVO toCategoryDetailResp(Category entity);

    CategoryTreeRespVO toCategoryTreeResp(Category entity);

    List<CategoryTreeRespVO> toCategoryTreeRespList(List<Category> entities);

    List<CategoryListRespVO> toCategoryListRespList(List<Category> entities);

    CategoryDTO toCategoryRemote(Category entity);

    List<CategoryDTO> toCategoryRemoteList(List<Category> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    Category toCategory(CategorySaveReqVO reqVO);

    default List<CategoryTreeRespVO> buildCategoryTree(List<Category> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<CategoryTreeRespVO> treeRespVOList = toCategoryTreeRespList(entities);
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

    CategoryTreeDTO toCategoryTreeRemote(Category entity);

    List<CategoryTreeDTO> toCategoryTreeRemoteList(List<Category> entities);

    default List<CategoryTreeDTO> buildCategoryTreeRemote(List<Category> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<CategoryTreeDTO> treeDtoList = toCategoryTreeRemoteList(entities);
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