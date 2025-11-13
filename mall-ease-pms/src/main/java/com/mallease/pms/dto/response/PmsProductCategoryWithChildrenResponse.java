package com.mallease.pms.dto.response;

import com.mallease.pms.pojo.PmsProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品分类响应类（包含子分类）
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductCategoryWithChildrenResponse {
    /**
     * 主键ID
     */
    private Long id;

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

    /**
     * 子分类列表
     */
    private List<PmsProductCategory> children;
}
