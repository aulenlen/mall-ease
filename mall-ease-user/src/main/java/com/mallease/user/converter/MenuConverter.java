package com.mallease.user.converter;

import com.mallease.user.model.client.cmd.MenuCmd;
import com.mallease.user.model.client.vo.MenuTreeVO;
import com.mallease.user.model.client.vo.MenuVO;
import com.mallease.user.model.data.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单转换器
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    /**
     * Entity → VO
     */
    MenuVO entityToVo(Menu entity);

    /**
     * Entity List → VO List
     */
    List<MenuVO> entityListToVoList(List<Menu> entities);

    List<MenuTreeVO> entityListToVoTree(List<Menu> entities);

    /**
     * Entity → TreeVO
     */
    MenuTreeVO entityToTreeVo(Menu entity);

    /**
     * Entity List → TreeVO List
     */
    List<MenuTreeVO> entityListToTreeVoList(List<Menu> entities);

    /**
     * Cmd → Entity
     */
    Menu cmdToEntity(MenuCmd cmd);

    /**
     * Cmd → Entity (更新)
     */
    void updateEntityFromCmd(@MappingTarget Menu entity, MenuCmd cmd);

    /**
     * 将扁平列表构建为树形结构
     *
     * @param entities 扁平分类列表
     * @return 树形结构（只返回顶级节点）
     */
    default List<MenuTreeVO> buildTree(List<Menu> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<MenuTreeVO> menuTree = entityListToTreeVoList(entities);
        Map<Long, MenuTreeVO> menuTreeMap = menuTree.stream()
                .collect(Collectors.toMap(MenuTreeVO::getId, menu -> menu));

        ArrayList<MenuTreeVO> roots = new ArrayList<>();
        for (MenuTreeVO treeVO : menuTree) {
            Long parentId = treeVO.getParentId();
            if (parentId == null || parentId == 0L) {
                roots.add(treeVO);
            } else {
                MenuTreeVO parent = menuTreeMap.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(treeVO);
                } else {
                    roots.add(treeVO);
                }
            }
        }
        return roots;
    }
}