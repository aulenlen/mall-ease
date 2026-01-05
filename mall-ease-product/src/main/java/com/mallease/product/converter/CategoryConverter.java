package com.mallease.product.converter;

import com.mallease.product.model.client.cmd.CategoryCmd;
import com.mallease.product.model.client.vo.CategoryDetailVO;

import com.mallease.product.model.client.vo.CategoryListVO;

import com.mallease.product.model.client.vo.CategoryTreeVO;
import com.mallease.product.model.data.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类转换器
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Mapper(componentModel = "spring")
public interface CategoryConverter {

    /**
     * Entity → ListVO
     */
    CategoryListVO entityToListVo(Category entity);

    /**
     * Entity → DetailVO
     */
    CategoryDetailVO entityToDetailVo(Category entity);

    /**
     * Entity → TreeVO
     */
    CategoryTreeVO entityToTreeVo(Category entity);

    /**
     * 辅助方法：entityToTreeVo 的列表版本，供 buildTree 使用
     */
    List<CategoryTreeVO> entityListToTreeVoList(List<Category> entities);
    List<CategoryListVO> entityListToListVoList(List<Category> entities);

    /**
     * SaveCmd → Entity（创建场景，path/level 由 Service 层计算）
     */
    @Mapping(target = "deleted", constant = "0")
    Category saveCmdToEntity(CategoryCmd cmd);

    /**
     * SaveCmd → Entity（更新场景，parentId 修改需 Service 层特殊处理）
     */
    void updateEntityFromCmd(@MappingTarget Category entity, CategoryCmd cmd);

    /**
     * 将扁平列表构建为树形结构
     *
     * @param entities 扁平分类列表
     * @return 树形结构（只返回顶级节点）
     */
    default List<CategoryTreeVO> buildTree(List<Category> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 转换为 TreeVO 并建立 ID -> VO 的映射
        List<CategoryTreeVO> treeVoList = entityListToTreeVoList(entities);
        Map<Long, CategoryTreeVO> idMap = treeVoList.stream()
                .collect(Collectors.toMap(CategoryTreeVO::getId, vo -> vo));

        // 2. 构建父子关系
        List<CategoryTreeVO> roots = new ArrayList<>();
        for (CategoryTreeVO vo : treeVoList) {
            Long parentId = vo.getParentId();
            if (parentId == null || parentId == 0L) {
                // 顶级节点
                roots.add(vo);
            } else {
                // 找到父节点并添加到其 children
                CategoryTreeVO parent = idMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    // 父节点不在列表中，作为顶级节点处理
                    roots.add(vo);
                }
            }
        }

        return roots;
    }
}