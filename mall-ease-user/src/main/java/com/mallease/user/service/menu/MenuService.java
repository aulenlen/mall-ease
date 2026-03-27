package com.mallease.user.service.menu;

import com.mallease.user.controller.admin.menu.vo.MenuRespVO;
import com.mallease.user.dal.entity.Menu;

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

    /**
     * 创建菜单
     * @param menu 菜单对象
     * @return 影响的行数
     */
    Integer create(Menu menu);

    /**
     * 更新菜单
     * @param menu 菜单对象
     * @return 影响的行数
     */
    Integer update(Menu menu);

    /**
     * 删除菜单
     * @param id 菜单ID
     * @return 影响的行数
     */
    Integer delete(Long id);

    /**
     * 批量删除菜单
     * @param ids 菜单ID列表
     * @return 影响的行数
     */
    int deleteBatch(List<Long> ids);

    /**
     * 通过ID获取详情
     * @param id 菜单ID
     * @return 菜单
     */
    Menu getById(Long id);
}