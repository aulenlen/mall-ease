package com.mallease.common.constant;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-07 22:56
 **/
public interface AuthConstant {
    /**
     * JWT存储权限前缀
     */
    String AUTHORITY_PREFIX = "ROLE_";

    /**
     * JWT存储权限属性
     */
    String AUTHORITY_CLAIM_NAME = "authorities";

    /**
     * 后台管理client_id
     */
    String ADMIN_CLIENT_ID = "admin-app";

    /**
     * 前台商城client_id
     */
    String PORTAL_CLIENT_ID = "portal-app";

    /**
     * 后台管理接口路径匹配
     */
    String ADMIN_URL_PATTERN = "/mall-admin/**";

    /**
     * Redis缓存权限规则（路径->资源）
     */
    String PATH_RESOURCE_MAP = "auth:pathResourceMap";

    /**
     * 认证信息Http请求头
     */
    String JWT_TOKEN_HEADER = "Authorization";

    /**
     * JWT令牌前缀
     */
    String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * 用户信息Http请求头
     */
    String USER_TOKEN_HEADER = "user";

    /**
     * sa-token session中存储的会员信息
     */
    String STP_MEMBER_INFO = "memberInfo";

    /**
     * sa-token session中存储的后台管理员信息
     */
    String STP_ADMIN_INFO = "adminInfo";

    /**
     * Sa-Token 多账号体系 - 管理员登录类型
     */
    String LOGIN_TYPE_ADMIN = "admin";

    /**
     * Sa-Token 多账号体系 - 会员登录类型
     */
    String LOGIN_TYPE_MEMBER = "member";
}
