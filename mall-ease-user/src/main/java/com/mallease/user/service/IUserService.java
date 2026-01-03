package com.mallease.user.service;

import com.mallease.user.pojo.*;

import java.util.List;

/**
 * @author: Aulen
 * @description: 统一用户服务接口
 * @create: 2025-11-09 21:37
 **/
public interface IUserService {
    /**
     * 根据用户名获取管理员
     */
    UserAdmin getAdminByUsername(String username);

    /**
     * 根据ID获取管理员
     */
    UserAdmin getAdminById(Long id);

    /**
     * 根据用户名获取会员
     */
    UserMember getMemberByUsername(String username);

    /**
     * 根据ID获取会员
     */
    UserMember getMemberById(Long id);

    /**
     * 根据ID获取权限
     */
    List<UserResource> getResourceList(Long adminId);

    /**
     * 获取登录用户信息
     *
     * @return
     */
    UserAdmin getCurrentAdmin();

    /**
     * 获取登录用户的菜单
     * @param adminId
     * @return
     */
    List<UserMenu> getCurrentMenus(Long adminId);

    /**
     * 获取登录用户的角色
     * @param adminId
     * @return
     */
    List<UserRole> getCurrentRoles(Long adminId);
}
