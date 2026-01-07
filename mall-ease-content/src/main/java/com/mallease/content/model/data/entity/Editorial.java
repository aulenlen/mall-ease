package com.mallease.content.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 编辑精选表
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Data
public class Editorial {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 系列名称
     */
    private String seriesName;

    /**
     * 期号
     */
    private Integer volumeNo;

    /**
     * 标题
     */
    private String title;

    /**
     * 摘要
     */
    private String subTitle;

    /**
     * 封面图URL
     */
    private String coverPic;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 作者
     */
    private String author;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 状态：0-草稿 1-已发布 2-已下架
     */
    private Integer status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记：0-未删除 1-已删除
     */
    private Integer deleted;
}
