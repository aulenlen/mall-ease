package com.mallease.user.controller.admin.resource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存资源命令（创建/更新统一）
 * 使用 Validation Groups 区分创建和更新的校验规则：
 * - Create.class: 创建时的校验组
 * - Update.class: 更新时的校验组
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "保存资源命令")
public class ResourceReqVO {

    /**
     * 创建时的校验组
     */
    public interface Create {
    }

    /**
     * 更新时的校验组
     */
    public interface Update {
    }

    @Schema(description = "资源ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时资源ID不能为空")
    private Long id;

    @Schema(description = "资源名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "资源名称不能为空")
    @Size(max = 200, message = "资源名称长度不能超过200个字符")
    private String name;

    @Schema(description = "资源URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "资源URL不能为空")
    @Size(max = 200, message = "资源URL长度不能超过200个字符")
    private String url;

    @Schema(description = "资源描述")
    @Size(max = 500, message = "资源描述长度不能超过500个字符")
    private String description;

    @Schema(description = "资源分类ID")
    private Long categoryId;
}