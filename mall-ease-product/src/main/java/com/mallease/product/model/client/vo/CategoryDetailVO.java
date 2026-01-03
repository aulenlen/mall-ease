package com.mallease.product.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品分类详情视图对象
 * <p>
 * 用于分类详情展示，包含完整信息和关联数据
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "商品分类详情视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDetailVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "父分类名称（由Service层填充）")
    private String parentName;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "层级路径（物化路径）")
    private String path;

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

    @Schema(description = "分类大图URL")
    private String image;

    @Schema(description = "SEO关键词")
    private String keywords;

    @Schema(description = "分类描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "更新人")
    private String updater;

    // ========================================================================
    // 关联数据（由 Service 层填充）
    // ========================================================================

    @Schema(description = "面包屑路径（从根到当前分类的名称列表）")
    private List<BreadcrumbItem> breadcrumb;

    @Schema(description = "关联的规格组ID列表")
    private List<Long> specGroupIds;

    @Schema(description = "关联的参数组ID列表")
    private List<Long> paramGroupIds;

    /**
     * 面包屑项（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "面包屑项")
    public static class BreadcrumbItem {

        @Schema(description = "分类ID")
        private Long id;

        @Schema(description = "分类名称")
        private String name;

        @Schema(description = "层级深度")
        private Integer level;
    }
}