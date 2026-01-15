package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 模板预览结果VO
 *
 * @author: Aulen
 * @create: 2026-01-13
 */
@Data
@Schema(description = "模板预览结果")
public class TemplatePreviewVO {

    @Schema(description = "统计摘要")
    private TemplateSummary summary;

    @Schema(description = "可新增属性ID全集（供前端勾选）")
    private List<Long> addAttrIds;

    @Schema(description = "新增样例列表（最多10条）")
    private List<TemplateAttrSample> sampleAddList;

    @Schema(description = "删除样例列表（最多10条，仅 replace 模式）")
    private List<TemplateAttrSample> sampleRemoveList;

    @Schema(description = "跳过样例列表（最多10条，仅 merge 模式）")
    private List<TemplateAttrSample> sampleSkipList;

    @Schema(description = "追踪ID（用于 apply 时传入）")
    private String traceId;

    /**
     * 统计摘要
     */
    @Data
    @Schema(description = "统计摘要")
    public static class TemplateSummary {

        @Schema(description = "新增数量")
        private Integer addCount;

        @Schema(description = "删除数量（仅 replace 模式）")
        private Integer removeCount;

        @Schema(description = "跳过数量（仅 merge 模式，已存在的属性）")
        private Integer skipCount;
    }

    /**
     * 属性样例
     */
    @Data
    @Schema(description = "属性样例")
    public static class TemplateAttrSample {

        @Schema(description = "属性ID")
        private Long attrId;

        @Schema(description = "属性名称")
        private String attrName;

        @Schema(description = "属性类型：0-参数 1-规格")
        private Integer type;

        @Schema(description = "分组名称")
        private String groupName;
    }
}
