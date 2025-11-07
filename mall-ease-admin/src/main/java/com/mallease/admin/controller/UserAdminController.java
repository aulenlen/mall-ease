package com.mallease.admin.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.admin.dto.request.UmsAdminLoginRequest;
import com.mallease.admin.service.UmsAdminService;
import com.mallease.common.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        return null;
    }
}
