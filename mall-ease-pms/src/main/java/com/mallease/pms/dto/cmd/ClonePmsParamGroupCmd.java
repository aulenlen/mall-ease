package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 克隆参数组命令
 *
 * @author: Aulen
 * @create: 2025-12-17
 */
@Data
@Schema(description = "克隆参数组命令")
public class ClonePmsParamGroupCmd {

    @NotNull(message = "参数组ID不能为空")
    @Schema(description = "要克隆的参数组ID")
    private Long paramGroupId;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "目标分类ID")
    private Long categoryId;

    @Schema(description = "新参数组名称（可选，默认为原名称_副本）")
    private String newName;
}