package com.mallease.product.controller.admin.attribute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 分类属性关联保存请求
 */
@Data
@Schema(description = "分类属性关联保存请求")
public class CategoryAttributeRelationSaveReqVO {

    @Schema(description = "关联ID（更新时使用）")
    private Long id;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID")
    private Long categoryId;

    @NotNull(message = "属性ID不能为空")
    @Schema(description = "属性ID")
    private Long attrId;

    @Schema(description = "在该分类下的分组名称")
    private String groupName;

    @Schema(description = "排序值")
    private Integer sort = 0;

    @Schema(description = "在该分类下是否必填：0-否 1-是")
    private Integer required = 0;

    @Schema(description = "分类特定选项（覆盖全局选项）")
    private List<String> options;
}