package com.mallease.user.controller;

import com.mallease.common.api.R;
import com.mallease.user.pojo.*;
import com.mallease.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-09 21:40
 **/
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * 示例：获取当前登录用户信息
     * 当admin模块通过Feign调用此接口时，可以通过StpUtil获取当前登录用户信息
     */
    @GetMapping("/admin/info")
    public R<Map<String, Object>> getCurrentAdmin() {
        try {
            // 从session中获取管理员信息
            UserAdmin admin = userService.getCurrentAdmin();
            List<UserRole> roleList = userService.getCurrentRoles(admin.getId());
            List<String> roles = roleList.stream().map(UserRole::getName).toList();
            Map<String, Object> data = new HashMap<>();
            List<UserMenu> menus = userService.getCurrentMenus(admin.getId());
            data.put("username", admin.getUsername());
            data.put("icon", admin.getIcon());
            data.put("roles",roles);
            data.put("menus",menus);
            return R.success(data);
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return R.failed("获取用户信息失败: " + e.getMessage());
        }
    }

    // 管理员接口
    @GetMapping("/admin/username/{username}")
    public R<UserAdmin> getAdminByUsername(@PathVariable String username) {
        UserAdmin admin = userService.getAdminByUsername(username);
        return R.success(admin);
    }

    @GetMapping("/admin/{id}")
    public R<UserAdmin> getAdminById(@PathVariable Long id) {
        UserAdmin admin = userService.getAdminById(id);
        return R.success(admin);
    }

    // 会员接口
    @GetMapping("/member/username/{username}")
    public R<UserMember> getMemberByUsername(@PathVariable String username) {
        UserMember member = userService.getMemberByUsername(username);
        return R.success(member);
    }

    @GetMapping("/member/{id}")
    public R<UserMember> getMemberById(@PathVariable Long id) {
        UserMember member = userService.getMemberById(id);
        return R.success(member);
    }

    @GetMapping("/admin/resource/{adminId}")
    public R<List<UserResource>> getResourceList(@PathVariable Long adminId) {
        List<UserResource> resourceList = userService.getResourceList(adminId);
        return R.success(resourceList);
    }
}
