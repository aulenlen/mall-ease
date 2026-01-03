package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规格值表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class SpecValue {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属规格ID
     */
    private Long specId;

    /**
     * 规格值，如"黑色"、"128GB"、"XL"
     */
    private String value;

    /**
     * 图片URL（颜色/图片类型规格可用）
     */
    private String image;

    /**
     * 颜色代码（颜色类型规格可用），如"#000000"
     */
    private String colorCode;

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
}