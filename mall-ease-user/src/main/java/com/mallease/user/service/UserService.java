package com.mallease.user.service;

import com.mallease.user.model.data.*;

import java.util.List;

/**
 * @author: Aulen
 * @description: 统一用户服务接口
 * @create: 2025-11-09 21:37
 **/
public interface UserService {
    /**
     * 根据用户名获取管理员
     */
    Admin getAdminByUsername(String username);

    /**
     * 根据ID获取管理员
     */
    Admin getAdminById(Long id);

    /**
     * 根据用户名获取会员
     */
    Member getMemberByUsername(String username);

    /**
     * 根据ID获取会员
     */
    Member getMemberById(Long id);

    /**
     * 根据ID获取权限
     */
    List<Resource> getResourceList(Long adminId);

    /**
     * 获取登录用户信息
     *
     * @return
     */
    Admin getCurrentAdmin();

    /**
     * 获取登录用户的菜单
     * @param adminId
     * @return
     */
    List<Menu> getCurrentMenus(Long adminId);

    /**
     * 获取登录用户的角色
     * @param adminId
     * @return
     */
    List<Role> getCurrentRoles(Long adminId);

    /**
     * 添加管理员
     */
    int create(Admin admin);

    /**
     * 更新管理员信息
     */
    int update(Long id, Admin admin);

    /**
     * 删除管理员
     */
    int delete(Long id);

    /**
     * 分页查询管理员
     */
    List<Admin> list(String username, Integer status, Integer pageSize, Integer pageNum);

    /**
     * 修改管理员状态
     */
    int updateStatus(Long id, Integer status);

    /**
     * 给管理员分配角色
     */
    int updateRole(Long adminId, List<Long> roleIds);

    /**
     * 获取指定管理员的角色列表
     */
    List<Role> getRoleList(Long adminId);
}
