package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规格组表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class SpecGroup {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 规格组名称，如"手机规格"、"服装规格"
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

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
