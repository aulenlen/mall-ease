package com.mallease.user.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Token 返回值
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Data
@Schema(description = "Token 返回值")
public class TokenRespVO {

    @Schema(description = "访问令牌", example = "e3d8a7f0-6b1c-4b17-8f88-01a9d5c4e2b1")
    private String token;

    @Schema(description = "Token 前缀", example = "Bearer ")
    private String tokenHead;
}
