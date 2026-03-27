package com.mallease.user.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 为管理员分配角色命令
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "为管理员分配角色命令")
public class AllocRoleReqVO {

    @Schema(description = "管理员ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "管理员ID不能为空")
    private Long adminId;

    @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "角色ID列表不能为空")
    private List<Long> roleIds;
}