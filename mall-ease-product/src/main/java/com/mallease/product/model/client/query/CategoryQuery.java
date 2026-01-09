package com.mallease.product.model.client.query;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 商品分类查询对象
 * <p>
 * 支持多维度组合查询：父分类、层级、状态、导航显示、关键字等
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品分类查询对象")
public class CategoryQuery extends BaseQuery {

    @Schema(description = "分类名称或关键字（模糊查询）")
    private String keyword;

    @Schema(description = "父分类ID（精确查询直接子分类）")
    private Long parentId;

    @Schema(description = "层级深度: 0-一级, 1-二级, 2-三级")
    private Integer level;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "是否导航栏显示: 0-否, 1-是")
    private Integer isNav;

    @Schema(description = "是否返回树形结构（默认false返回扁平列表）")
    private Boolean tree = false;

    @Schema(description = "是否包含子孙分类（配合parentId使用，使用物化路径查询）")
    private Boolean includeDescendants = false;
}