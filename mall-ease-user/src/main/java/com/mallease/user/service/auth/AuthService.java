package com.mallease.user.service.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.user.controller.admin.auth.vo.AdminLoginReqVO;
import com.mallease.user.controller.portal.auth.vo.MemberLoginReqVO;
import com.mallease.user.controller.portal.auth.vo.MemberRegisterReqVO;

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
    SaTokenInfo loginAdmin(AdminLoginReqVO reqVO);

    /**
     * 会员登录
     */
    SaTokenInfo loginMember(MemberLoginReqVO reqVO);

    /**
     * 会员注册
     *
     * @param reqVO 注册请求
     * @return 注册成功后自动登录的 Token 信息
     */
    SaTokenInfo registerMember(MemberRegisterReqVO reqVO);
}
