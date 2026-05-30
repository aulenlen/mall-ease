package com.mallease.content.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容媒体资源。
 */
@Data
public class Media {

    /**
     * 主键ID。
     */
    private Long id;

    /**
     * 分组ID，NULL表示未分组。
     */
    private Long groupId;

    /**
     * 文件 hash 值。
     */
    private String hash;

    /**
     * 原始文件名。
     */
    private String originalName;

    /**
     * 可访问的完整 URL。
     */
    private String url;

    /**
     * 缩略图 URL。
     */
    private String thumbnailUrl;

    /**
     * 媒体类型：IMAGE/VIDEO/OTHER。
     */
    private String mediaType;

    /**
     * 文件字节数。
     */
    private Long fileSize;

    /**
     * 扩展名。
     */
    private String extension;

    /**
     * 创建人。
     */
    private String creator;

    /**
     * 更新人。
     */
    private String updater;

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
