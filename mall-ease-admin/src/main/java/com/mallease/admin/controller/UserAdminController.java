package com.mallease.admin.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.admin.dto.request.UmsAdminLoginRequest;
import com.mallease.admin.pojo.UmsAdmin;
import com.mallease.admin.pojo.UmsMenu;
import com.mallease.admin.pojo.UmsRole;
import com.mallease.admin.service.UmsAdminService;
import com.mallease.common.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-07 14:53
 * 后台管理端
 **/
@RestController
@RequestMapping("/admin")
@Slf4j
@CrossOrigin
public class UserAdminController {
    @Autowired
    private UmsAdminService adminService;
    @Value("${sa-token.token-prefix}")
    private String tokenHead;

    /**
     * 登录后返回token
     */
    @PostMapping("/login")
    public R login(@Validated @RequestBody UmsAdminLoginRequest request) {
        SaTokenInfo tokenInfo = adminService.login(request.getUsername(), request.getPassword());
        if (tokenInfo == null) {
            return R.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", tokenInfo.getTokenValue());
        tokenMap.put("tokenHead", tokenHead + " ");
        return R.success(tokenMap);
    }

    /**
     * 获取登录用户信息
     * @return
     */
    @GetMapping("/info")
    public R info() {
        UmsAdmin admin = adminService.getCurrentAdmin();
        List<UmsRole> roleList = adminService.getCurrentRoles(admin.getId());
        List<String> roles = roleList.stream().map(UmsRole::getName).toList();
        Map<String, Object> data = new HashMap<>();
        List<UmsMenu> menus = adminService.getCurrentMenus(admin.getId());
        data.put("username", admin.getUsername());
        data.put("icon", admin.getIcon());
        data.put("roles",roles);
        data.put("menus",menus);
        return R.success(data);
    }
}
