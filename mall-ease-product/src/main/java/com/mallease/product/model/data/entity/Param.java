package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 参数定义表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class Param {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属参数组ID
     */
    private Long groupId;

    /**
     * 参数名称，如"CPU型号"、"屏幕尺寸"
     */
    private String name;

    /**
     * 单位，如"英寸"、"mAh"、"g"
     */
    private String unit;

    /**
     * 录入方式：0-手动输入 1-从列表选择
     */
    private Integer inputType;

    /**
     * 可选值列表（逗号分隔），录入方式为1时使用
     */
    private String inputList;

    /**
     * 是否必填：0-否 1-是
     */
    private Integer isRequired;

    /**
     * 是否可搜索：0-否 1-是
     */
    private Integer isSearchable;

    /**
     * 是否亮点参数（商品列表展示）：0-否 1-是
     */
    private Integer isHighlight;

    /**
     * 是否可对比：0-否 1-是
     */
    private Integer isComparable;

    /**
     * 排序
     */
    private Integer sort;

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
