package com.mallease.user.service;

import com.mallease.user.model.data.Menu;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author: Aulen
 * @create: 2026-01-24
 */
public interface MenuService {

    /**
     * 根据ID列表批量查询菜单
     *
     * @param ids ID列表
     * @return 菜单列表
     */
    List<Menu> listByIds(List<Long> ids);

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    List<Menu> listAll();

    /**
     * 根据父级ID查询子菜单
     *
     * @param parentId 父级ID
     * @return 子菜单列表
     */
    List<Menu> listByParentId(Long parentId);
}