package com.mallease.gateway.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.mallease.common.constant.AuthConstant;
import com.mallease.common.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-11 01:18
 **/
/**
 * 自定义权限验证接口扩展
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 从 session 中获取用户信息
        try {
            Object adminInfoObj = StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
            if (adminInfoObj instanceof UserDto) {
                UserDto adminInfo = (UserDto) adminInfoObj;
                List<String> permissionList = adminInfo.getPermissionList();
                return permissionList != null ? permissionList : new ArrayList<>();
            }
        } catch (Exception e) {
            // 如果获取失败，返回空列表
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 如果不需要角色验证，可以返回空列表
        return new ArrayList<>();
    }
}