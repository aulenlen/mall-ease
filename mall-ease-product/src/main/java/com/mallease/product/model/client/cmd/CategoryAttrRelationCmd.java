package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 分类-属性关联命令对象
 *
 * @author: Aulen
 * @create: 2026-01-12
 */
@Data
@Schema(description = "分类-属性关联命令对象")
public class CategoryAttrRelationCmd {

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
