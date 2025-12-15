package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新规格定义命令对象
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "更新规格定义命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePmsSpecCmd {

    @Schema(description = "规格ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规格ID不能为空")
    private Long id;

    @Schema(description = "所属规格组ID")
    private Long groupId;

    @Schema(description = "规格名称")
    @Size(max = 64, message = "规格名称长度不能超过64个字符")
    private String name;

    @Schema(description = "展示类型: 0-文字, 1-颜色块, 2-图片")
    @Min(value = 0, message = "展示类型值必须为0、1或2")
    @Max(value = 2, message = "展示类型值必须为0、1或2")
    private Integer displayType;

    @Schema(description = "是否必选: 0-否, 1-是")
    @Min(value = 0, message = "是否必选值必须为0或1")
    @Max(value = 1, message = "是否必选值必须为0或1")
    private Integer isRequired;

    @Schema(description = "是否可搜索: 0-否, 1-是")
    @Min(value = 0, message = "是否可搜索值必须为0或1")
    @Max(value = 1, message = "是否可搜索值必须为0或1")
    private Integer isSearchable;

    @Schema(description = "是否可筛选: 0-否, 1-是")
    @Min(value = 0, message = "是否可筛选值必须为0或1")
    @Max(value = 1, message = "是否可筛选值必须为0或1")
    private Integer isFilterable;

    @Schema(description = "排序值，越小越靠前")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;
}