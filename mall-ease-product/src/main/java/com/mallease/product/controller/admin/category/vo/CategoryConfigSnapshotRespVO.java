package com.mallease.product.controller.admin.category.vo;

import com.mallease.product.controller.admin.brand.vo.BrandListRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.mallease.product.controller.admin.attribute.vo.CategoryAttributeRelationRespVO;

import java.util.List;

/**
 * 分类快照响应
 *
 * @author: Aulen
 * @create: 2026-01-13
 */
@Data
@Schema(description = "分类快照")
public class CategoryConfigSnapshotRespVO {

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
    private List<CategoryAttributeRelationRespVO> specs;

    @Schema(description = "参数列表（type=0）")
    private List<CategoryAttributeRelationRespVO> params;

    @Schema(description = "品牌列表")
    private List<BrandListRespVO> brands;

    @Schema(description = "追踪ID")
    private String traceId;
}
