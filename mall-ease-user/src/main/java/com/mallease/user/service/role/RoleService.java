package com.mallease.user.service.role;

import com.github.pagehelper.PageInfo;
import com.mallease.user.dal.entity.Role;

import java.util.List;

/**
 * 角色管理服务接口
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param role 角色对象
     * @return 角色ID
     */
    Long create(Role role);

    /**
     * 更新角色
     *
     * @param role 角色对象
     * @return 影响行数
     */
    int update(Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     * @return 影响行数
     */
    int batchDelete(List<Long> ids);

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色
     */
    Role getById(Long id);

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    List<Role> listAll();

    /**
     * 根据状态查询角色列表
     *
     * @param status 启用状态：0->禁用；1->启用
     * @return 角色列表
     */
    List<Role> listByStatus(Integer status);

    /**
     * 分页查询角色列表
     *
     * @param keyword 关键词（角色名称）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 角色列表
     */
    List<Role> list(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 更新角色状态
     *
     * @param id 角色ID
     * @param status 启用状态：0->禁用；1->启用
     * @return 影响行数
     */
    int updateStatus(Long id, Integer status);

    /**
     * 根据ID列表批量查询角色
     *
     * @param ids ID列表
     * @return 角色列表
     */
    List<Role> listByIds(List<Long> ids);

    /**
     * 根据角色ID列表查询资源ID列表
     *
     * @param roleIds 角色ID列表
     * @return 资源ID列表
     */
    List<Long> getResourceIdsByRoleIds(List<Long> roleIds);

    /**
     * 根据角色ID列表查询菜单ID列表
     *
     * @param roleIds 角色ID列表
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleIds(List<Long> roleIds);

    /**
     * 给角色分配菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     * @return 影响行数
     */
    int allocMenu(Long roleId, List<Long> menuIds);

    /**
     * 给角色分配资源
     *
     * @param roleId      角色ID
     * @param resourceIds 资源ID列表
     * @return 影响行数
     */
    int allocResource(Long roleId, List<Long> resourceIds);
}