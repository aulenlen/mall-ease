package com.mallease.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.auth.model.cmd.MemberRegisterCmd;
import com.mallease.auth.model.query.LoginCmd;

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
    SaTokenInfo loginAdmin(LoginCmd request);

    /**
     * 会员登录
     */
    SaTokenInfo loginMember(LoginCmd request);

    /**
     * 会员注册
     *
     * @param cmd 注册命令
     * @return 注册成功后自动登录的 Token 信息
     */
    SaTokenInfo registerMember(MemberRegisterCmd cmd);
}
