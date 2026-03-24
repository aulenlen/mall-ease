package com.mallease.product.controller.admin.brand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 分类品牌关联保存请求
 *
 * @author: Aulen
 * @create: 2026-01-12
 */
@Data
@Schema(description = "分类品牌关联保存请求")
public class CategoryBrandRelationSaveReqVO {

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID")
    private Long categoryId;

    @NotNull(message = "品牌ID不能为空")
    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌ID列表（批量关联时使用）")
    private List<Long> brandIds;
}
