package com.mallease.pms.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品属性列表响应类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductAttributeListVO {

    /**
     * 属性ID
     */
    private Long id;

    /**
     * 产品属性分类ID
     */
    private Long productAttributeCategoryId;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性选择类型：0->唯一；1->单选；2->多选
     */
    private Integer selectType;

    /**
     * 选择类型名称
     */
    private String selectTypeName;

    /**
     * 属性录入方式：0->手工录入；1->从列表中选取
     */
    private Integer inputType;

    /**
     * 录入方式名称
     */
    private String inputTypeName;

    /**
     * 可选值列表，以逗号隔开
     */
    private String inputList;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 分类筛选样式：0->普通；1->颜色
     */
    private Integer filterType;

    /**
     * 筛选样式名称
     */
    private String filterTypeName;

    /**
     * 检索类型：0->不需要进行检索；1->关键字检索；2->范围检索
     */
    private Integer searchType;

    /**
     * 检索类型名称
     */
    private String searchTypeName;

    /**
     * 属性的类型：0->规格；1->参数
     */
    private Integer type;

    /**
     * 属性类型名称
     */
    private String typeName;
}
