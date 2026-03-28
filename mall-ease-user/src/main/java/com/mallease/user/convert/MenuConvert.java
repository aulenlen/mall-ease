package com.mallease.user.convert;

import com.mallease.user.controller.admin.menu.vo.MenuReqVO;
import com.mallease.user.controller.admin.menu.vo.MenuTreeRespVO;
import com.mallease.user.controller.admin.menu.vo.MenuRespVO;
import com.mallease.user.dal.entity.Menu;
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
public interface MenuConvert {

    /**
     * Entity → VO
     */
    MenuRespVO toMenuResp(Menu entity);

    /**
     * Entity List → VO List
     */
    List<MenuRespVO> toMenuRespList(List<Menu> entities);

    List<MenuTreeRespVO> buildMenuRespTree(List<Menu> entities);

    /**
     * Entity → TreeVO
     */
    MenuTreeRespVO toMenuTreeResp(Menu entity);

    /**
     * Entity List → TreeVO List
     */
    List<MenuTreeRespVO> toMenuTreeRespList(List<Menu> entities);

    /**
     * Cmd → Entity
     */
    Menu toMenu(MenuReqVO cmd);

    /**
     * Cmd → Entity (更新)
     */
    void copyToMenu(@MappingTarget Menu entity, MenuReqVO cmd);

    /**
     * 将扁平列表构建为树形结构
     *
     * @param entities 扁平分类列表
     * @return 树形结构（只返回顶级节点）
     */
    default List<MenuTreeRespVO> buildMenuTree(List<Menu> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }

        List<MenuTreeRespVO> menuTree = toMenuTreeRespList(entities);
        Map<Long, MenuTreeRespVO> menuTreeMap = menuTree.stream()
                .collect(Collectors.toMap(MenuTreeRespVO::getId, menu -> menu));

        ArrayList<MenuTreeRespVO> roots = new ArrayList<>();
        for (MenuTreeRespVO treeVO : menuTree) {
            Long parentId = treeVO.getParentId();
            if (parentId == null || parentId == 0L) {
                roots.add(treeVO);
            } else {
                MenuTreeRespVO parent = menuTreeMap.get(parentId);
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