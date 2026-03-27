package com.mallease.user.controller.admin.role.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 为角色分配资源权限命令
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "为角色分配资源权限命令")
public class AllocResourceReqVO {

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "资源ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "资源ID列表不能为空")
    private List<Long> resourceIds;
}