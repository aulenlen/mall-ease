package com.mallease.user.service;

import com.mallease.user.model.data.Admin;

/**
 * @author: Aulen
 * @description: 后台用户信息缓存操作
 * @create: 2025-11-10 14:27
 **/
public interface UserCacheService {
    /**
     * 删除后台用户缓存
     */
    void delAdmin(Long adminId);

    /**
     * 获取缓存后台用户信息
     */
    Admin getAdmin(Long adminId);

    /**
     * 设置缓存后台用户信息
     */
    void setAdmin(Admin admin);
}
