package com.mallease.pms.pojo;

import lombok.Data;

/**
 * 产品分类
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Data
public class PmsProductCategory {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 上机分类的编号：0表示一级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类级别：0->1级；1->2级
     */
    private Integer level;

    /**
     * 产品数量
     */
    private Integer productCount;

    /**
     * 产品单位
     */
    private String productUnit;

    /**
     * 是否显示在导航栏：0->不显示；1->显示
     */
    private Integer navStatus;

    /**
     * 显示状态：0->不显示；1->显示
     */
    private Integer showStatus;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 图标
     */
    private String icon;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 描述
     */
    private String description;
}






