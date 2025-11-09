package com.mallease.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.auth.dto.LoginRequest;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-09 19:09
 **/
public interface AuthService {
    /**
     * 统一登录接口
     */
    SaTokenInfo login(LoginRequest request);
}
