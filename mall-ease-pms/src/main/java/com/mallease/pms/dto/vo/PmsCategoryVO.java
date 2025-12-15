package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商品分类基础视图对象
 * <p>
 * 用于单个分类的基础信息展示
 *
 * @author: Aulen
 * @create: 2025-12-13
 */
@Schema(description = "商品分类基础视图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsCategoryVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "层级路径（物化路径）")
    private String path;

    @Schema(description = "层级深度: 0-一级, 1-二级, 2-三级")
    private Integer level;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "是否导航栏显示: 0-否, 1-是")
    private Integer isNav;

    @Schema(description = "导航显示名称")
    private String isNavName;

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
}