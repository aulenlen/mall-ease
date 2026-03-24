package com.mallease.product.controller.admin.attribute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 属性返回
 */
@Data
@Schema(description = "属性返回")
public class AttributeRespVO {

    @Schema(description = "属性ID")
    private Long id;

    @Schema(description = "属性名称")
    private String name;

    @Schema(description = "属性类型：0-参数 1-规格")
    private Integer type;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "录入方式：0-手工录入 1-预设选项")
    private Integer entryMethod;

    @Schema(description = "全局预设选项（JSON格式）")
    private String options;

    @Schema(description = "全局预设选项列表（解析后）")
    private List<String> optionList;

    @Schema(description = "是否可搜索：0-否 1-是")
    private Integer searchable;

    @Schema(description = "是否可筛选：0-否 1-是")
    private Integer filterable;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}