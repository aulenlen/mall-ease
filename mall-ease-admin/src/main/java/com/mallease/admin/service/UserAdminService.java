package com.mallease.admin.service;

import com.mallease.admin.dto.request.LoginRequestDto;

import java.util.Map;

/**
 * @author: Aulen
 * @description: 管理员服务接口
 * @create: 2025-11-10
 **/
public interface UserAdminService {
    /**
     * 管理员登录
     *
     * @param loginRequestDto 登录请求DTO
     * @return token信息
     */
    Map<String, String> login(LoginRequestDto loginRequestDto);

    /**
     * 获取当前登录用户信息
     * @return
     */
    Map<String, Object> getCurrentAdminInfo();
}
