package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新规格组命令对象
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "更新规格组命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePmsSpecGroupCmd {

    @Schema(description = "规格组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规格组ID不能为空")
    private Long id;

    @Schema(description = "规格组名称")
    @Size(max = 64, message = "规格组名称长度不能超过64个字符")
    private String name;

    @Schema(description = "排序值，越小越靠前")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = "状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status;
}