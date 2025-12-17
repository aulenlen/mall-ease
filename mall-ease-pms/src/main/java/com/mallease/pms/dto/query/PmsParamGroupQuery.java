package com.mallease.pms.dto.query;

import com.mallease.common.dto.query.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数组查询对象
 *
 * @author: Aulen
 * @create: 2025-12-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "参数组查询对象")
public class PmsParamGroupQuery extends BaseQuery {

    @Schema(description = "参数组名称(模糊查询)")
    private String keyword;
}
