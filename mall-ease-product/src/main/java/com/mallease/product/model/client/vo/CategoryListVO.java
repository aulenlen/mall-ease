package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品分类列表视图对象
 * <p>
 * 用于分类列表展示，精简字段以提升性能
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "商品分类列表视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryListVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "层级深度: 0-一级, 1-二级, 2-三级")
    private Integer level;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "是否导航栏显示: 0-否, 1-是")
    private Integer isNav;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "分类图标URL")
    private String icon;

    @Schema(description = "子分类数量（由Service层填充）")
    private Integer childCount;
}