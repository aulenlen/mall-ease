package com.mallease.user.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单树形视图对象
 * <p>
 * 用于树形结构展示，包含子菜单列表
 *
 * @author: Aulen
 * @create: 2026-01-23
 */
@Schema(description = "菜单树形视图对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuTreeVO {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "菜单标题")
    private String title;

    @Schema(description = "菜单层级：0->一级；1->二级；2->三级")
    private Integer level;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "前端名称")
    private String name;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "是否隐藏：0->不隐藏；1->隐藏")
    private Integer hidden;

    @Schema(description = "子菜单列表")
    private List<MenuTreeVO> children;
}