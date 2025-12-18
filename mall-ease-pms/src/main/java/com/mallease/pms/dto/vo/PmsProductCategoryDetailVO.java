package com.mallease.pms.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品分类详情响应类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductCategoryDetailVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 上级分类的编号：0表示一级分类
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
