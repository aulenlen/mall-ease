package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class Category {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 层级路径，格式如 /1/7/8/，用于高效查询子树
     * 查询所有子孙：WHERE path LIKE '/1/%'
     */
    private String path;

    /**
     * 层级深度：0=一级，1=二级，2=三级
     */
    private Integer level;

    /**
     * 启用状态：0-禁用 1-启用
     */
    private Integer enableStatus;

    /**
     * 是否导航栏显示：0-否 1-是
     */
    private Integer isNav;

    /**
     * 排序值，越小越靠前
     */
    private Integer sort;

    /**
     * 分类图标URL
     */
    private String icon;

    /**
     * 分类大图URL（用于专题页、banner等）
     */
    private String image;

    /**
     * SEO关键词
     */
    private String keywords;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;
}
