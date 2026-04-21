package com.mallease.product.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分类批量排序请求
 *
 * @author: Aulen
 * @create: 2026-04-21
 */
@Schema(description = "分类批量排序请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySortReqVO {

    @Schema(description = "父分类ID，0=顶级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "父分类ID不能为空")
    @Min(value = 0, message = "父分类ID不能为负数")
    private Long parentId;

    @Schema(description = "同级分类排序项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "排序项列表不能为空")
    @Size(max = 500, message = "单次排序数量不能超过500")
    @Valid
    private List<Item> items;

    @Schema(description = "排序项")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {

        @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "分类ID不能为空")
        @Min(value = 1, message = "分类ID必须为正整数")
        private Long id;

        @Schema(description = "排序值，越小越靠前", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "排序值不能为空")
        @Min(value = 0, message = "排序值不能为负数")
        private Integer sort;
    }
}
