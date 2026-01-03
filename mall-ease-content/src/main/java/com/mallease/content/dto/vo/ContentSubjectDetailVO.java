package com.mallease.content.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专题详情响应类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "专题详情响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSubjectDetailVO {

    /**
     * 专题ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 专题名称
     */
    private String title;

    /**
     * 专题图片
     */
    private String pic;

    /**
     * 关联产品数量
     */
    private Integer spuCount;

    /**
     * 推荐状态：0-不推荐 1-推荐
     */
    private Integer recommendStatus;

    /**
     * 推荐状态名称
     */
    private String recommendStatusName;

    /**
     * 专题内容
     */
    private String content;

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
     * 画册图片(逗号分割)
     */
    private String albumPics;

    /**
     * 专题描述
     */
    private String description;

    /**
     * 显示状态：0-不显示 1-显示
     */
    private Integer showStatus;

    /**
     * 显示状态名称
     */
    private String showStatusName;

    /**
     * 转发数
     */
    private Integer forwardCount;

    /**
     * 创建时间(格式化)
     */
    private String createTimeStr;
}
