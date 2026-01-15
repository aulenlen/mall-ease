package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模板应用结果VO
 *
 * @author: Aulen
 * @create: 2026-01-13
 */
@Data
@Schema(description = "模板应用结果")
public class TemplateApplyVO {

    @Schema(description = "统计摘要")
    private TemplatePreviewVO.TemplateSummary summary;

    @Schema(description = "追踪ID")
    private String traceId;

    @Schema(description = "是否成功")
    private Boolean success;
}
