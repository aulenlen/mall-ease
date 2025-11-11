package com.mallease.ums.controller;

import com.mallease.common.api.R;
import com.mallease.ums.pojo.*;
import com.mallease.ums.service.IUserService;
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
@RequestMapping("/ums")
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
            UmsAdmin admin = userService.getCurrentAdmin();
            List<UmsRole> roleList = userService.getCurrentRoles(admin.getId());
            List<String> roles = roleList.stream().map(UmsRole::getName).toList();
            Map<String, Object> data = new HashMap<>();
            List<UmsMenu> menus = userService.getCurrentMenus(admin.getId());
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
    public R<UmsAdmin> getAdminByUsername(@PathVariable String username) {
        UmsAdmin admin = userService.getAdminByUsername(username);
        return R.success(admin);
    }

    @GetMapping("/admin/{id}")
    public R<UmsAdmin> getAdminById(@PathVariable Long id) {
        UmsAdmin admin = userService.getAdminById(id);
        return R.success(admin);
    }

    // 会员接口
    @GetMapping("/member/username/{username}")
    public R<UmsMember> getMemberByUsername(@PathVariable String username) {
        UmsMember member = userService.getMemberByUsername(username);
        return R.success(member);
    }

    @GetMapping("/member/{id}")
    public R<UmsMember> getMemberById(@PathVariable Long id) {
        UmsMember member = userService.getMemberById(id);
        return R.success(member);
    }

    @GetMapping("/admin/resource/{adminId}")
    public R<List<UmsResource>> getResourceList(@PathVariable Long adminId) {
        List<UmsResource> resourceList = userService.getResourceList(adminId);
        return R.success(resourceList);
    }
}
