package com.mallease.pms.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规格值表
 * 说明：存储规格的具体可选值，如"黑色"、"128GB"、"XL"
 * 支持颜色代码和图片，用于前端展示颜色块或图片选择器
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class PmsSpecValue {
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