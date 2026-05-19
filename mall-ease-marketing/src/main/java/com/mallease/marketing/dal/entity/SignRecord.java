package com.mallease.marketing.dal.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员签到记录。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
public class SignRecord {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 签到日期
     */
    private LocalDate signDate;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 本次发放积分
     */
    private Integer integration;

    /**
     * 本次发放成长值
     */
    private Integer growth;

    /**
     * 奖励状态：0-待处理 1-已处理 2-处理失败。
     */
    private Integer rewardStatus;

    /**
     * 奖励幂等键
     */
    private String rewardBizKey;

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
