package com.mallease.common.util;

import cn.dev33.satoken.stp.StpLogic;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.remote.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 登录用户信息统一获取工具类（支持多账号体系）
 *
 * @author: Aulen
 * @create: 2025-11-19
 */
@Slf4j
public class LoginContextUtil {

    /**
     * 管理员账号体系的 StpLogic
     */
    private static final StpLogic stpAdminLogic = new StpLogic(AuthConstant.LOGIN_TYPE_ADMIN);

    /**
     * 会员账号体系的 StpLogic
     */
    private static final StpLogic stpMemberLogic = new StpLogic(AuthConstant.LOGIN_TYPE_MEMBER);

    /**
     * 获取当前登录用户的 UserDTO 对象（支持多账号体系）
     */
    private static UserDTO getCurrentUser() {
        if (!isWebContext()) {
            return null;
        }

        try {
            // 检查管理员账号体系
            if (stpAdminLogic.isLogin()) {
                Object adminInfo = stpAdminLogic.getSession().get(AuthConstant.STP_ADMIN_INFO);
                if (adminInfo instanceof UserDTO) {
                    return (UserDTO) adminInfo;
                }
            }

            // 检查会员账号体系
            if (stpMemberLogic.isLogin()) {
                Object memberInfo = stpMemberLogic.getSession().get(AuthConstant.STP_MEMBER_INFO);
                if (memberInfo instanceof UserDTO) {
                    return (UserDTO) memberInfo;
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("获取当前用户信息失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前操作人名称；系统任务等非登录场景统一返回 system
     */
    public static String getOperatorNameOrSystem() {
        return isWebContext() ? getUserName() : "system";
    }

    /**
     * 判断当前是否在 Web 上下文中
     */
    private static boolean isWebContext() {
        try {
            return RequestContextHolder.getRequestAttributes() != null;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * 获取用户ID（支持多账号体系）
     */
    public static Long getUserId() {
        try {
            if (stpAdminLogic.isLogin()) {
                return stpAdminLogic.getLoginIdAsLong();
            }
            if (stpMemberLogic.isLogin()) {
                return stpMemberLogic.getLoginIdAsLong();
            }
            return null;
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户名
     */
    public static String getUserName() {
        UserDTO userDto = getCurrentUser();
        return userDto != null ? userDto.getUsername() : null;
    }

    /**
     * 获取客户端ID（区分管理员和会员）
     */
    public static String getClientId() {
        UserDTO userDto = getCurrentUser();
        return userDto != null ? userDto.getClientId() : null;
    }

    /**
     * 获取当前用户的权限列表
     */
    public static java.util.List<String> getPermissionList() {
        UserDTO userDto = getCurrentUser();
        return userDto != null && userDto.getPermissionList() != null
                ? userDto.getPermissionList()
                : java.util.Collections.emptyList();
    }

    /**
     * 判断当前用户是否已登录（任一账号体系）
     */
    public static boolean isLogin() {
        return stpAdminLogic.isLogin() || stpMemberLogic.isLogin();
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        return stpAdminLogic.isLogin();
    }

    /**
     * 判断当前用户是否为会员
     */
    public static boolean isMember() {
        return stpMemberLogic.isLogin();
    }

    /**
     * 获取当前请求的Token值
     */
    public static String getTokenValue() {
        try {
            if (stpAdminLogic.isLogin()) {
                return stpAdminLogic.getTokenValue();
            }
            if (stpMemberLogic.isLogin()) {
                return stpMemberLogic.getTokenValue();
            }
            return null;
        } catch (Exception e) {
            log.warn("获取Token值失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前用户的所有角色（Sa-Token角色）
     */
    public static java.util.List<String> getRoleList() {
        try {
            if (stpAdminLogic.isLogin()) {
                return stpAdminLogic.getRoleList();
            }
            if (stpMemberLogic.isLogin()) {
                return stpMemberLogic.getRoleList();
            }
            return java.util.Collections.emptyList();
        } catch (Exception e) {
            log.warn("获取当前用户角色列表失败: {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 获取完整的 UserDTO 对象
     */
    public static UserDTO getUserDto() {
        return getCurrentUser();
    }
}
