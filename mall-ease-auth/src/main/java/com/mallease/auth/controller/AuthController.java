package com.mallease.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.auth.config.StpAdminUtil;
import com.mallease.auth.config.StpMemberUtil;
import com.mallease.auth.model.cmd.MemberRegisterCmd;
import com.mallease.auth.model.query.LoginCmd;
import com.mallease.auth.service.AuthService;
import com.mallease.common.api.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Tag(name = "认证管理", description = "用户登录认证相关接口")
@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${sa-token.token-prefix}")
    private String tokenHead;

    @Operation(summary = "后台管理员登录")
    @PostMapping("/admin/login")
    public R<Map<String, String>> adminLogin(@Validated @RequestBody LoginCmd request) {
        SaTokenInfo tokenInfo = authService.loginAdmin(request);
        return buildTokenResponse(tokenInfo);
    }

    @Operation(summary = "后台管理员退出登录")
    @PostMapping("/admin/logout")
    public R<Void> adminLogout() {
        StpAdminUtil.logout();
        return R.success(null);
    }

    @Operation(summary = "前台会员注册", description = "注册成功后自动登录并返回 Token")
    @PostMapping("/portal/register")
    public R<Map<String, String>> portalRegister(@Validated @RequestBody MemberRegisterCmd cmd) {
        SaTokenInfo tokenInfo = authService.registerMember(cmd);
        return buildTokenResponse(tokenInfo);
    }

    @Operation(summary = "前台会员登录")
    @PostMapping("/portal/login")
    public R<Map<String, String>> portalLogin(@Validated @RequestBody LoginCmd request) {
        SaTokenInfo tokenInfo = authService.loginMember(request);
        return buildTokenResponse(tokenInfo);
    }

    @Operation(summary = "前台会员退出登录")
    @PostMapping("/portal/logout")
    public R<Void> portalLogout() {
        StpMemberUtil.logout();
        return R.success(null);
    }

    private R<Map<String, String>> buildTokenResponse(SaTokenInfo tokenInfo) {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", tokenInfo.getTokenValue());
        tokenMap.put("tokenHead", tokenHead + " ");
        return R.success(tokenMap);
    }
}