package com.mallease.pms.dto.vo;

import com.mallease.pms.pojo.PmsProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品分类视图对象（包含子分类）
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "商品分类（包含子分类）")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductCategoryWithChildrenVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类级别(0:一级 1:二级)")
    private Integer level;

    @Schema(description = "商品数量")
    private Integer productCount;

    @Schema(description = "商品单位")
    private String productUnit;

    @Schema(description = "导航栏显示(0:不显示 1:显示)")
    private Integer navStatus;

    @Schema(description = "显示状态(0:不显示 1:显示)")
    private Integer showStatus;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "关键词")
    private String keywords;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "子分类列表")
    private List<PmsProductCategory> children;
}
