package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品分类树形视图对象
 * <p>
 * 用于树形结构展示，包含子分类列表
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "商品分类树形视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryTreeVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "层级深度: 0-一级, 1-二级, 2-三级")
    private Integer level;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    private Integer enableStatus;

    @Schema(description = "是否导航栏显示: 0-否, 1-是")
    private Integer isNav;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "分类图标URL")
    private String icon;

    @Schema(description = "分类大图URL")
    private String image;

    @Schema(description = "子分类列表")
    private List<CategoryTreeVO> children;
}
