package com.mallease.user.controller.admin.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.mallease.common.api.R;
import com.mallease.user.config.StpAdminUtil;
import com.mallease.user.controller.admin.auth.vo.AdminLoginReqVO;
import com.mallease.user.controller.admin.auth.vo.TokenRespVO;
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
 * 后台认证控制器。
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Tag(name = "管理端-认证", description = "后台管理员登录认证相关接口")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    @Value("${sa-token.token-prefix}")
    private String tokenHead;

    @Operation(summary = "后台管理员登录")
    @PostMapping("/login")
    public R<TokenRespVO> login(@Validated @RequestBody AdminLoginReqVO reqVO) {
        SaTokenInfo tokenInfo = authService.loginAdmin(reqVO);
        return buildTokenResponse(tokenInfo);
    }

    @Operation(summary = "后台管理员退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        StpAdminUtil.logout();
        return R.success(null);
    }

    private R<TokenRespVO> buildTokenResponse(SaTokenInfo tokenInfo) {
        TokenRespVO respVO = new TokenRespVO();
        respVO.setToken(tokenInfo.getTokenValue());
        respVO.setTokenHead(tokenHead);
        return R.success(respVO);
    }
}
