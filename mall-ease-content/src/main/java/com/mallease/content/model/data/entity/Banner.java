package com.mallease.content.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Banner轮播图表
 *
 * @author: Aulen
 * @create: 2025-01-01
 */
@Data
public class Banner {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * Banner名称（后台管理标识）
     */
    private String name;

    /**
     * 图片URL
     */
    private String pic;

    /**
     * 跳转类型：0-无跳转 1-活动页 2-商品详情(SPU) 3-专题页 4-外链 5-优选专区
     */
    private Integer type;

    /**
     * 跳转目标ID（type=1/2/3/5时使用）
     */
    private Long targetId;

    /**
     * 跳转链接（type=4外链时使用）
     */
    private String url;

    /**
     * 投放位置：home-首页
     */
    private String position;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 生效开始时间
     */
    private LocalDateTime startTime;

    /**
     * 生效结束时间
     */
    private LocalDateTime endTime;

    /**
     * 点击次数统计
     */
    private Integer clickCount;

    /**
     * 备注
     */
    private String note;

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