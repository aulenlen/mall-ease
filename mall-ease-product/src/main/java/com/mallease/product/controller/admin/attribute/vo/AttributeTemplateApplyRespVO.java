package com.mallease.product.controller.admin.attribute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 属性模板应用返回
 */
@Data
@Schema(description = "属性模板应用返回")
public class AttributeTemplateApplyRespVO {

    @Schema(description = "统计摘要")
    private AttributeTemplatePreviewRespVO.TemplateSummary summary;

    @Schema(description = "追踪ID")
    private String traceId;

    @Schema(description = "是否成功")
    private Boolean success;
}