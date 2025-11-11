package com.mallease.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.auth.dto.LoginRequest;
import com.mallease.auth.service.AuthService;
import com.mallease.common.api.R;
import com.mallease.common.api.ResultCode;
import com.mallease.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Aulen
 * @description:
 * @create: 2025-11-09 21:52
 **/
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @Value("${sa-token.token-prefix}")
    private String tokenHead;

    /**
     * 统一登录接口
     * 支持管理员和普通用户登录
     *
     * @param request 登录请求，userType字段可选：
     *                - 不传或传"admin"：管理员登录
     *                - 传"member"：普通用户登录
     */
    @PostMapping("/login")
    public R<Map<String, String>> login(@Validated @RequestBody LoginRequest request) {
        SaTokenInfo tokenInfo = authService.login(request);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", tokenInfo.getTokenValue());
        tokenMap.put("tokenHead", tokenHead + " ");
        return R.success(tokenMap);
    }

    /**
     * 管理员登录（便捷接口，内部调用统一登录）
     */
    @PostMapping("/admin/login")
    public R<Map<String, String>> adminLogin(@Validated @RequestBody LoginRequest request) {
        request.setUserType("admin");
        R<Map<String, String>> R = login(request);
        if (R == null || !ResultCode.SUCCESS.getCode().equals(R.getCode())) {
            throw new ApiException(R != null ? R.getMessage() : "登录失败");
        }
        return R;
    }

    /**
     * 普通用户登录（便捷接口，内部调用统一登录）
     */
    @PostMapping("/portal/login")
    public R<Map<String, String>> portalLogin(@Validated @RequestBody LoginRequest request) {
        request.setUserType("member");
        return login(request);
    }
}
