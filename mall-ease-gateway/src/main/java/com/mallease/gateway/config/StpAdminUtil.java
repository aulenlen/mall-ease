package com.mallease.gateway.config;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import com.mallease.common.constant.AuthConstant;

/**
 * 管理员认证工具类（Sa-Token 多账号体系）
 *
 * @author: Aulen
 * @create: 2026-01-24
 */
public class StpAdminUtil {

    private StpAdminUtil() {
    }

    /**
     * 管理员账号体系的 StpLogic
     */
    public static StpLogic stpLogic = new StpLogic(AuthConstant.LOGIN_TYPE_ADMIN);

    public static StpLogic getStpLogic() {
        return stpLogic;
    }

    public static void checkLogin() {
        stpLogic.checkLogin();
    }

    public static boolean isLogin() {
        return stpLogic.isLogin();
    }

    public static Object getLoginId() {
        return stpLogic.getLoginId();
    }

    public static Long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }

    public static SaTokenInfo getTokenInfo() {
        return stpLogic.getTokenInfo();
    }

    public static SaSession getSession() {
        return stpLogic.getSession();
    }

    public static SaSession getSession(boolean isCreate) {
        return stpLogic.getSession(isCreate);
    }

    public static void checkPermission(String permission) {
        stpLogic.checkPermission(permission);
    }

    public static void checkPermissionOr(String... permissions) {
        stpLogic.checkPermissionOr(permissions);
    }

    public static boolean hasPermission(String permission) {
        return stpLogic.hasPermission(permission);
    }
}