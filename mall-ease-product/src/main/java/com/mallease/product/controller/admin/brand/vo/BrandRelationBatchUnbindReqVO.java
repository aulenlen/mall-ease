package com.mallease.product.controller.admin.brand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 品牌批量解绑请求
 *
 * @author: Aulen
 * @create: 2026-01-13
 */
@Data
@Schema(description = "品牌批量解绑请求")
public class BrandRelationBatchUnbindReqVO {

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID")
    private Long categoryId;

    @NotEmpty(message = "品牌ID列表不能为空")
    @Schema(description = "品牌ID列表")
    private List<Long> brandIds;
}
