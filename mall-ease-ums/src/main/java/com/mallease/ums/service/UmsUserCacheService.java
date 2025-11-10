package com.mallease.ums.service;

import com.mallease.ums.pojo.UmsAdmin;

/**
 * @author: Aulen
 * @description: 后台用户信息缓存操作
 * @create: 2025-11-10 14:27
 **/
public interface UmsUserCacheService {
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
