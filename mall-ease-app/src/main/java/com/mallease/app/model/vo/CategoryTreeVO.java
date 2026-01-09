package com.mallease.app.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID，0表示顶级分类")
    private Long parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "层级深度：0=一级，1=二级，2=三级")
    private Integer level;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "分类大图URL（用于专题页、banner等）")
    private String image;

    @Schema(description = "排序值，越小越靠前")
    private Integer sort;

    @Schema(description = "子分类")
    private List<CategoryTreeVO> children;

    @Schema(description = "推荐商品")
    private List<HomeRecommendVO> recommends;
}
