package com.mallease.admin.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.admin.pojo.UmsAdmin;
import com.mallease.admin.pojo.UmsMenu;
import com.mallease.admin.pojo.UmsRole;

import java.util.List;

/**
 * @author: Aulen
 * @description: 后台管理端service
 * @create: 2025-11-07 18:26
 **/
public interface UmsAdminService {
    /**
     * 登录校验
     *
     * @param username
     * @param password
     */
    SaTokenInfo login(String username, String password);

    /**
     * 通过用户名获取后台管理员
     *
     * @param username
     * @return
     */
    UmsAdmin getUmsAdminByUsername(String username);

    /**
     * 获取登录用户信息
     *
     * @return
     */
    UmsAdmin getCurrentAdmin();

    /**
     * 获取登录用户的角色
     * @param adminId
     * @return
     */
    List<UmsRole> getCurrentRoles(Long adminId);

    /**
     * 获取登录用户的菜单
     * @param adminId
     * @return
     */
    List<UmsMenu> getCurrentMenus(Long adminId);

    /**
     * 用户登出
     */
    void logout();
}
