package com.mallease.marketing.model.data.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 秒杀活动表
 *
 * @author: Aulen
 * @create: 2026-01-04
 */
@Data
public class FlashActivity {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动开始日期
     */
    private LocalDate startDate;

    /**
     * 活动结束日期
     */
    private LocalDate endDate;

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