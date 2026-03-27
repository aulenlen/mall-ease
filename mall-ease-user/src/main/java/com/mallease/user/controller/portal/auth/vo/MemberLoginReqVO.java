package com.mallease.user.controller.portal.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员登录请求
 *
 * @author: Aulen
 * @create: 2025-11-09 21:45
 */
@Data
@Schema(description = "会员登录请求")
public class MemberLoginReqVO {

    @Schema(description = "用户名", example = "member001")
    private String username;

    @Schema(description = "密码", example = "123456")
    private String password;
}
