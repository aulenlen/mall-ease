package com.mallease.user.controller;

import com.mallease.common.api.R;
import com.mallease.user.model.data.*;
import com.mallease.user.service.UserService;
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
    private UserService userService;

    /**
     * 示例：获取当前登录用户信息
     * 当admin模块通过Feign调用此接口时，可以通过StpUtil获取当前登录用户信息
     */
    @GetMapping("/admin/info")
    public R<Map<String, Object>> getCurrentAdmin() {
        try {
            // 从session中获取管理员信息
            Admin admin = userService.getCurrentAdmin();
            List<Role> roleList = userService.getCurrentRoles(admin.getId());
            List<String> roles = roleList.stream().map(Role::getName).toList();
            Map<String, Object> data = new HashMap<>();
            List<Menu> menus = userService.getCurrentMenus(admin.getId());
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
    public R<Admin> getAdminByUsername(@PathVariable String username) {
        Admin admin = userService.getAdminByUsername(username);
        return R.success(admin);
    }

    @GetMapping("/admin/{id}")
    public R<Admin> getAdminById(@PathVariable Long id) {
        Admin admin = userService.getAdminById(id);
        return R.success(admin);
    }

    // 会员接口
    @GetMapping("/member/username/{username}")
    public R<Member> getMemberByUsername(@PathVariable String username) {
        Member member = userService.getMemberByUsername(username);
        return R.success(member);
    }

    @GetMapping("/member/{id}")
    public R<Member> getMemberById(@PathVariable Long id) {
        Member member = userService.getMemberById(id);
        return R.success(member);
    }

    @GetMapping("/admin/resource/{adminId}")
    public R<List<Resource>> getResourceList(@PathVariable Long adminId) {
        List<Resource> resourceList = userService.getResourceList(adminId);
        return R.success(resourceList);
    }
}
