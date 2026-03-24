package com.mallease.product.controller.admin.attribute.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 属性分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "属性分页查询请求")
public class AttributePageReqVO extends BaseQuery {

    @Schema(description = "属性类型：0-参数 1-规格")
    private Integer type;

    @Schema(description = "属性名称（模糊查询）")
    private String keyword;

    @Schema(description = "是否可搜索：0-否 1-是")
    private Integer searchable;

    @Schema(description = "是否可筛选：0-否 1-是")
    private Integer filterable;
}