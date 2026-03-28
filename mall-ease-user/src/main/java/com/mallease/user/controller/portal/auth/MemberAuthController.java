package com.mallease.user.controller.portal.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.common.api.R;
import com.mallease.user.config.StpMemberUtil;
import com.mallease.user.controller.portal.auth.vo.MemberLoginReqVO;
import com.mallease.user.controller.portal.auth.vo.MemberRegisterReqVO;
import com.mallease.user.controller.portal.auth.vo.TokenRespVO;
import com.mallease.user.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台会员认证控制器。
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Tag(name = "前台-会员认证", description = "前台会员登录认证相关接口")
@RestController
@RequestMapping("/portal/auth")
@RequiredArgsConstructor
public class MemberAuthController {

    private final AuthService authService;

    @Value("${sa-token.token-prefix}")
    private String tokenHead;

    @Operation(summary = "前台会员注册", description = "注册成功后自动登录并返回 Token")
    @PostMapping("/register")
    public R<TokenRespVO> register(@Validated @RequestBody MemberRegisterReqVO reqVO) {
        SaTokenInfo tokenInfo = authService.registerMember(reqVO);
        return buildTokenResponse(tokenInfo);
    }

    @Operation(summary = "前台会员登录")
    @PostMapping("/login")
    public R<TokenRespVO> login(@Validated @RequestBody MemberLoginReqVO reqVO) {
        SaTokenInfo tokenInfo = authService.loginMember(reqVO);
        return buildTokenResponse(tokenInfo);
    }

    @Operation(summary = "前台会员退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        StpMemberUtil.logout();
        return R.success(null);
    }

    private R<TokenRespVO> buildTokenResponse(SaTokenInfo tokenInfo) {
        TokenRespVO respVO = new TokenRespVO();
        respVO.setToken(tokenInfo.getTokenValue());
        respVO.setTokenHead(tokenHead + " ");
        return R.success(respVO);
    }
}
