package com.mallease.content.dal.entity;

import lombok.Data;

import java.util.Date;

/**
 * 专题表
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
public class Subject {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 专题分类ID
     */
    private Long categoryId;

    /**
     * 专题标题
     */
    private String title;

    /**
     * 专题主图
     */
    private String pic;

    /**
     * 关联产品数量
     */
    private Integer spuCount;

    /**
     * 推荐状态：0->不推荐；1->推荐
     */
    private Integer recommendStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 收藏数量
     */
    private Integer collectCount;

    /**
     * 阅读数量
     */
    private Integer readCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 画册图片用逗号分割
     */
    private String albumPics;

    /**
     * 专题描述
     */
    private String description;

    /**
     * 显示状态：0->不显示；1->显示
     */
    private Integer showStatus;

    /**
     * 专题内容
     */
    private String content;

    /**
     * 转发数
     */
    private Integer forwardCount;

    /**
     * 专题分类名称
     */
    private String categoryName;
}
