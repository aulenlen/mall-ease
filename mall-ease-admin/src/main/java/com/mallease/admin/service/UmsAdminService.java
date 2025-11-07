package com.mallease.admin.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.admin.pojo.UmsAdmin;

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
}
