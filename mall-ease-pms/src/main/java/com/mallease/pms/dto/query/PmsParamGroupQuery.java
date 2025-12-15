package com.mallease.pms.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 参数组查询条件
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Data
@Schema(description = "参数组查询条件")
public class PmsParamGroupQuery {

    @Schema(description = "参数组名称（模糊查询）")
    private String name;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;
}
