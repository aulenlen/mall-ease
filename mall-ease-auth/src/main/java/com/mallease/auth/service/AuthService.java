package com.mallease.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.auth.model.query.LoginQuery;

/**
 * 认证服务接口
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
public interface AuthService {

    /**
     * 管理员登录
     */
    SaTokenInfo loginAdmin(LoginQuery request);

    /**
     * 会员登录
     */
    SaTokenInfo loginMember(LoginQuery request);
}
