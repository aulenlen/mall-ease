package com.mallease.content.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "轮播图响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerVO {
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
     * 排序值，越小越靠前
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
}
