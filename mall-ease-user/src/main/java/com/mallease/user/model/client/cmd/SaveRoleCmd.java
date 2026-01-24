package com.mallease.user.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存角色命令（创建/更新统一）
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
@Schema(description = "保存角色命令")
public class SaveRoleCmd {

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

    @Schema(description = "角色ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时角色ID不能为空")
    private Long id;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "角色名称不能为空")
    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    private String name;

    @Schema(description = "角色描述")
    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    private String description;

    @Schema(description = "启用状态：0->禁用；1->启用")
    @Min(value = 0, message = "启用状态值必须为0或1")
    @Max(value = 1, message = "启用状态值必须为0或1")
    private Integer status;

    @Schema(description = "排序")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;
}