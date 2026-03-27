package com.mallease.user.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 后台登录请求
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Data
@Schema(description = "后台登录请求")
public class AdminLoginReqVO {

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "密码", example = "123456")
    private String password;
}
