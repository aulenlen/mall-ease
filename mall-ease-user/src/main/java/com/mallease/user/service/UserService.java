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
}
