package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分类快照VO（聚合接口返回）
 *
 * @author: Aulen
 * @create: 2026-01-13
 */
@Data
@Schema(description = "分类快照")
public class CategoryConfigSnapshotVO {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类路径")
    private String path;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "是否叶子分类")
    private Boolean isLeaf;

    @Schema(description = "规格列表（type=1）")
    private List<CategoryAttributeVO> specs;

    @Schema(description = "参数列表（type=0）")
    private List<CategoryAttributeVO> params;

    @Schema(description = "品牌列表")
    private List<BrandListVO> brands;

    @Schema(description = "追踪ID")
    private String traceId;
}
