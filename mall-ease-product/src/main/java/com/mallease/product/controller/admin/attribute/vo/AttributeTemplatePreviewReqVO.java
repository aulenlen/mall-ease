package com.mallease.product.controller.admin.attribute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 属性模板预览请求
 */
@Data
@Schema(description = "属性模板预览请求")
public class AttributeTemplatePreviewReqVO {

    @NotNull(message = "模板分类ID不能为空")
    @Schema(description = "模板分类ID")
    private Long templateCategoryId;

    @NotNull(message = "目标分类ID不能为空")
    @Schema(description = "目标分类ID（必须是叶子分类）")
    private Long targetCategoryId;

    @NotBlank(message = "模式不能为空")
    @Pattern(regexp = "^(replace|merge)$", message = "模式只能是 replace 或 merge")
    @Schema(description = "模式：replace-替换（清空后重建） merge-合并（增量添加）")
    private String mode;

    @NotBlank(message = "范围不能为空")
    @Pattern(regexp = "^(spec|param|both)$", message = "范围只能是 spec/param/both")
    @Schema(description = "范围：spec-规格 param-参数 both-全部")
    private String scope;
}