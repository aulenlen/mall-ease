package com.mallease.product.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类保存请求
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "分类保存请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySaveReqVO {

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

    @Schema(description = "分类ID（创建时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时分类ID不能为空")
    private Long id;

    @Schema(description = "父分类ID，0表示顶级分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时父分类ID不能为空")
    @Min(value = 0, message = "父分类ID不能为负数")
    @Builder.Default
    private Long parentId = 0L;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = Create.class, message = "创建时分类名称不能为空")
    @Size(max = 64, message = "分类名称长度不能超过64个字符")
    private String name;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    @Builder.Default
    private Integer enableStatus = 1;

    @Schema(description = "是否导航栏显示: 0-否, 1-是")
    @Min(value = 0, message = "导航显示值必须为0或1")
    @Max(value = 1, message = "导航显示值必须为0或1")
    @Builder.Default
    private Integer isNav = 0;

    @Schema(description = "排序值，越小越靠前")
    @Min(value = 0, message = "排序值不能小于0")
    @Builder.Default
    private Integer sort = 0;

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
