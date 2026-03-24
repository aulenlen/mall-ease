package com.mallease.product.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品属性定义表（全局属性池）
 *
 * @author: Aulen
 * @create: 2026-01-11
 */
@Data
public class Attribute {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性类型：0-参数 1-规格
     */
    private Integer type;

    /**
     * 单位
     */
    private String unit;

    /**
     * 录入方式：0-手工录入 1-预设选项
     */
    private Integer entryMethod;

    /**
     * 全局预设选项列表（JSON格式，策略1使用）
     */
    private String options;

    /**
     * 是否可搜索：0-否 1-是
     */
    private Integer searchable;

    /**
     * 是否可筛选：0-否 1-是
     */
    private Integer filterable;

    /**
     * 逻辑删除：0-未删除 1-已删除
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
}
