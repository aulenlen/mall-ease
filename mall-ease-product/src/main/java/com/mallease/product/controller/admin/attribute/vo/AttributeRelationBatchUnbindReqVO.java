package com.mallease.product.controller.admin.attribute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 分类属性批量解绑请求
 */
@Data
@Schema(description = "分类属性批量解绑请求")
public class AttributeRelationBatchUnbindReqVO {

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID")
    private Long categoryId;

    @NotEmpty(message = "属性ID列表不能为空")
    @Schema(description = "属性ID列表")
    private List<Long> attrIds;
}