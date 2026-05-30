package com.mallease.content.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容媒体分组。
 */
@Data
public class MediaGroup {

    /**
     * 主键ID。
     */
    private Long id;

    /**
     * 分组名称。
     */
    private String name;

    /**
     * 排序值，越小越靠前。
     */
    private Integer sort;

    /**
     * 0-未删除 1-已删除。
     */
    private Integer deleted;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    private LocalDateTime updateTime;
}
