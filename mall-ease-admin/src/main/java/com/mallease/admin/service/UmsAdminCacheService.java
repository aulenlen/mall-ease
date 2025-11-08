package com.mallease.admin.service;

import com.mallease.admin.pojo.UmsAdmin;

/**
 * @author: Aulen
 * @description: 后台用户缓存操作接口
 * @create: 2025-11-08 02:06
 **/
public interface UmsAdminCacheService {
    /**
     * 删除后台用户缓存
     */
    void delAdmin(Long adminId);

    /**
     * 获取缓存后台用户信息
     */
    UmsAdmin getAdmin(Long adminId);

    /**
     * 设置缓存后台用户信息
     */
    void setAdmin(UmsAdmin admin);
}
