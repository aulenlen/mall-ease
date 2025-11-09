package com.mallease.ums.service;

import com.mallease.ums.pojo.UmsAdmin;
import com.mallease.ums.pojo.UmsMember;
import com.mallease.ums.pojo.UmsResource;

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
    UmsAdmin getAdminByUsername(String username);

    /**
     * 根据ID获取管理员
     */
    UmsAdmin getAdminById(Long id);

    /**
     * 根据用户名获取会员
     */
    UmsMember getMemberByUsername(String username);

    /**
     * 根据ID获取会员
     */
    UmsMember getMemberById(Long id);

    /**
     * 根据ID获取权限
     */
    List<UmsResource> getResourceList(Long adminId);
}
