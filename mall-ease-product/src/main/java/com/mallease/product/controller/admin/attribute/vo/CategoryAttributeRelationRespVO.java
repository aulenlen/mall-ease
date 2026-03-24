package com.mallease.product.controller.admin.attribute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分类属性关联返回
 */
@Data
@Schema(description = "分类属性关联返回")
public class CategoryAttributeRelationRespVO {

    @Schema(description = "关联ID")
    private Long relationId;

    @Schema(description = "属性ID")
    private Long attrId;

    @Schema(description = "属性名称")
    private String attrName;

    @Schema(description = "属性类型：0-参数 1-规格")
    private Integer type;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "录入方式：0-手工录入 1-预设选项")
    private Integer entryMethod;

    @Schema(description = "是否可搜索：0-否 1-是")
    private Integer searchable;

    @Schema(description = "是否可筛选：0-否 1-是")
    private Integer filterable;

    @Schema(description = "在该分类下的分组名称")
    private String groupName;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "在该分类下是否必填：0-否 1-是")
    private Integer required;

    @Schema(description = "有效选项列表（优先关联表选项，其次全局选项）")
    private List<String> optionList;

    @Schema(description = "全局选项（JSON格式）")
    private String globalOptions;

    @Schema(description = "分类特定选项（JSON格式）")
    private String categoryOptions;
}