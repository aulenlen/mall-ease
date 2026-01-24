package com.mallease.user.controller;

import com.mallease.common.api.R;
import com.mallease.user.model.data.*;
import com.mallease.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Tag(name = "用户管理", description = "管理员和会员信息查询")
@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前登录管理员信息", description = "返回用户名、头像、角色列表、菜单列表")
    @GetMapping("/admin/info")
    public R<Map<String, Object>> getCurrentAdmin() {
        try {
            Admin admin = userService.getCurrentAdmin();
            List<Role> roleList = userService.getCurrentRoles(admin.getId());
            List<String> roles = roleList.stream().map(Role::getName).toList();
            Map<String, Object> data = new HashMap<>();
            List<Menu> menus = userService.getCurrentMenus(admin.getId());
            data.put("username", admin.getUsername());
            data.put("icon", admin.getIcon());
            data.put("roles", roles);
            data.put("menus", menus);
            return R.success(data);
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return R.failed("获取用户信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据用户名查询管理员", description = "内部调用")
    @GetMapping("/admin/username/{username}")
    public R<Admin> getAdminByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        Admin admin = userService.getAdminByUsername(username);
        return R.success(admin);
    }

    @Operation(summary = "根据ID查询管理员", description = "内部调用")
    @GetMapping("/admin/{id}")
    public R<Admin> getAdminById(
            @Parameter(description = "管理员ID") @PathVariable Long id) {
        Admin admin = userService.getAdminById(id);
        return R.success(admin);
    }

    @Operation(summary = "根据用户名查询会员", description = "内部调用")
    @GetMapping("/member/username/{username}")
    public R<Member> getMemberByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        Member member = userService.getMemberByUsername(username);
        return R.success(member);
    }

    @Operation(summary = "根据ID查询会员", description = "内部调用")
    @GetMapping("/member/{id}")
    public R<Member> getMemberById(
            @Parameter(description = "会员ID") @PathVariable Long id) {
        Member member = userService.getMemberById(id);
        return R.success(member);
    }

    @Operation(summary = "获取管理员资源列表", description = "内部调用，用于权限校验")
    @GetMapping("/admin/resource/{adminId}")
    public R<List<Resource>> getResourceList(
            @Parameter(description = "管理员ID") @PathVariable Long adminId) {
        List<Resource> resourceList = userService.getResourceList(adminId);
        return R.success(resourceList);
    }
}
