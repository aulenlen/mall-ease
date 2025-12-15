package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新商品分类命令对象
 * <p>
 * 支持部分更新，null 值字段不会被更新。
 * 注意：parentId 的修改会触发 path 和 level 的重新计算（移动分类）
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "更新商品分类命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePmsCategoryCmd {

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Long id;

    @Schema(description = "父分类ID，0表示顶级分类（修改会触发分类移动）")
    @Min(value = 0, message = "父分类ID不能为负数")
    private Long parentId;

    @Schema(description = "分类名称")
    @Size(max = 64, message = "分类名称长度不能超过64个字符")
    private String name;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status;

    @Schema(description = "是否导航栏显示: 0-否, 1-是")
    @Min(value = 0, message = "导航显示值必须为0或1")
    @Max(value = 1, message = "导航显示值必须为0或1")
    private Integer isNav;

    @Schema(description = "排序值，越小越靠前")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = "分类图标URL")
    @Size(max = 255, message = "图标URL长度不能超过255个字符")
    private String icon;

    @Schema(description = "分类大图URL（用于专题页、banner等）")
    @Size(max = 255, message = "大图URL长度不能超过255个字符")
    private String image;

    @Schema(description = "SEO关键词")
    @Size(max = 255, message = "关键词长度不能超过255个字符")
    private String keywords;

    @Schema(description = "分类描述")
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;
}