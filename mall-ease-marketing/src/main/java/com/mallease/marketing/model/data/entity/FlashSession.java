package com.mallease.marketing.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 秒杀场次表
 *
 * @author: Aulen
 * @create: 2026-01-04
 */
@Data
public class FlashSession {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 场次名称
     */
    private String name;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

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
