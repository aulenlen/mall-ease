package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规格定义表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class Spec {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属规格组ID
     */
    private Long groupId;

    /**
     * 规格名称，如"颜色"、"内存"、"尺码"
     */
    private String name;

    /**
     * 展示类型：0-文字 1-颜色块 2-图片
     */
    private Integer displayType;

    /**
     * 是否必选：0-否 1-是
     */
    private Integer isRequired;

    /**
     * 是否可搜索：0-否 1-是
     */
    private Integer isSearchable;

    /**
     * 是否可筛选：0-否 1-是
     */
    private Integer isFilterable;

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
