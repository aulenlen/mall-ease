package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建参数组命令对象
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "创建参数组命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePmsParamGroupCmd {

    @Schema(description = "参数组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数组名称不能为空")
    @Size(max = 64, message = "参数组名称长度不能超过64个字符")
    private String name;

    @Schema(description = "排序值，越小越靠前")
    @Min(value = 0, message = "排序值不能小于0")
    @Builder.Default
    private Integer sort = 0;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    @Builder.Default
    private Integer status = 1;

    @Schema(description = "关联的分类ID（可选，传入则自动绑定到该分类）")
    private Long categoryId;
}