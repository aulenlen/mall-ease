package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

/**
 * 模板应用命令
 *
 * @author: Aulen
 * @create: 2026-01-13
 */
@Data
@Schema(description = "模板应用命令")
public class TemplateApplyCmd {

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

    @NotBlank(message = "traceId不能为空")
    @Schema(description = "追踪ID（来自 preview 返回）")
    private String traceId;

    @Schema(description = "要新增的属性ID列表（为空则全部新增）")
    private List<Long> selectedAddAttrIds;
}
