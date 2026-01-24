package com.mallease.common.util;

import cn.dev33.satoken.stp.StpUtil;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录用户信息统一获取工具类
 *
 * @author: Aulen
 * @create: 2025-11-19
 */
@Slf4j
public class LoginContextUtil {

    /**
     * 获取当前登录用户的 UserDTO 对象
     *
     * @return UserDTO，未登录返回null
     */
    private static UserDTO getCurrentUser() {
        try {
            if (!StpUtil.isLogin()) {
                return null;
            }

            Object adminInfo = StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
            if (adminInfo instanceof UserDTO) {
                return (UserDTO) adminInfo;
            }

            Object memberInfo = StpUtil.getSession().get(AuthConstant.STP_MEMBER_INFO);
            if (memberInfo instanceof UserDTO) {
                return (UserDTO) memberInfo;
            }

            return null;
        } catch (Exception e) {
            log.warn("获取当前用户信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public static String getUserName() {
        UserDTO userDto = getCurrentUser();
        return userDto != null ? userDto.getUsername() : null;
    }

    /**
     * 获取客户端ID（区分管理员和会员）
     *
     * @return 客户端ID（admin-app 或 portal-app）
     */
    public static String getClientId() {
        UserDTO userDto = getCurrentUser();
        return userDto != null ? userDto.getClientId() : null;
    }

    /**
     * 获取当前用户的权限列表
     *
     * @return 权限列表
     */
    public static java.util.List<String> getPermissionList() {
        UserDTO userDto = getCurrentUser();
        return userDto != null && userDto.getPermissionList() != null
            ? userDto.getPermissionList()
            : java.util.Collections.emptyList();
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return true-已登录 false-未登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 判断当前用户是否为管理员
     *
     * @return true-管理员 false-会员或未登录
     */
    public static boolean isAdmin() {
        String clientId = getClientId();
        return AuthConstant.ADMIN_CLIENT_ID.equals(clientId);
    }

    /**
     * 判断当前用户是否为会员
     *
     * @return true-会员 false-管理员或未登录
     */
    public static boolean isMember() {
        String clientId = getClientId();
        return AuthConstant.PORTAL_CLIENT_ID.equals(clientId);
    }

    /**
     * 获取当前请求的Token值
     *
     * @return Token值
     */
    public static String getTokenValue() {
        try {
            return StpUtil.getTokenValue();
        } catch (Exception e) {
            log.warn("获取Token值失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前用户的所有角色（Sa-Token角色）
     *
     * @return 角色列表
     */
    public static java.util.List<String> getRoleList() {
        try {
            return StpUtil.getRoleList();
        } catch (Exception e) {
            log.warn("获取当前用户角色列表失败: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 获取完整的 UserDTO 对象
     *
     * @return UserDto对象，未登录返回null
     */
    public static UserDTO getUserDto() {
        return getCurrentUser();
    }
}
