package com.mallease.user.controller.admin.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存菜单命令（创建/更新统一）
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
@Schema(description = "保存菜单命令")
public class MenuReqVO {

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

    @Schema(description = "菜单ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时菜单ID不能为空")
    private Long id;

    @Schema(description = "父级菜单ID（0表示一级菜单）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = {Create.class, Update.class}, message = "父级菜单ID不能为空")
    private Long parentId;

    @Schema(description = "菜单标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "菜单标题不能为空")
    @Size(max = 100, message = "菜单标题长度不能超过100个字符")
    private String title;

    @Schema(description = "菜单层级：0->一级；1->二级；2->三级")
    @Min(value = 0, message = "菜单层级不能小于0")
    @Max(value = 2, message = "菜单层级不能大于2")
    private Integer level;

    @Schema(description = "排序")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = "前端名称")
    @Size(max = 100, message = "前端名称长度不能超过100个字符")
    private String name;

    @Schema(description = "菜单图标")
    @Size(max = 200, message = "菜单图标长度不能超过200个字符")
    private String icon;

    @Schema(description = "是否隐藏：0->不隐藏；1->隐藏")
    @Min(value = 0, message = "隐藏状态值必须为0或1")
    @Max(value = 1, message = "隐藏状态值必须为0或1")
    private Integer hidden;
}