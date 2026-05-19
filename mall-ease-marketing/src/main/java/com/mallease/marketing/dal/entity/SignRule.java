package com.mallease.marketing.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 连续签到奖励规则。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
public class SignRule {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 触发奖励的连续天数，1 表示每日基础奖励
     */
    private Integer continuousDays;

    /**
     * 奖励积分
     */
    private Integer integration;

    /**
     * 奖励成长值
     */
    private Integer growth;

    /**
     * 备注
     */
    private String remark;

    /**
     * 启用状态：0-禁用 1-启用
     */
    private Integer enableStatus;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

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
