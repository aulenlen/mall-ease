package com.mallease.gateway.config;

import cn.dev33.satoken.stp.StpInterface;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展（支持多账号体系）
 *
 * @author: Aulen
 * @create: 2025-11-11
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (AuthConstant.LOGIN_TYPE_ADMIN.equals(loginType)) {
            return getAdminPermissions();
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    private List<String> getAdminPermissions() {
        try {
            Object adminInfoObj = StpAdminUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
            if (adminInfoObj instanceof UserDTO adminInfo) {
                List<String> permissionList = adminInfo.getPermissionList();
                return permissionList != null ? permissionList : new ArrayList<>();
            }
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }
}