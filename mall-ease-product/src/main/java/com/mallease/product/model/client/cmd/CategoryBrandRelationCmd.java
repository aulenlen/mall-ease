package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 分类-品牌关联命令对象
 *
 * @author: Aulen
 * @create: 2026-01-12
 */
@Data
@Schema(description = "分类-品牌关联命令对象")
public class CategoryBrandRelationCmd {

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "品牌ID（单个关联时使用）")
    private Long brandId;

    @Schema(description = "品牌ID列表（批量关联时使用）")
    private List<Long> brandIds;
}
