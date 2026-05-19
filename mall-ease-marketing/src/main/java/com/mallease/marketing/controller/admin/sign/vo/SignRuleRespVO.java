package com.mallease.marketing.controller.admin.sign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到规则响应对象。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
@Schema(description = "签到规则响应对象")
public class SignRuleRespVO {

    /**
     * 主键ID。
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 触发奖励的连续签到天数，1 表示每日基础奖励。
     */
    @Schema(description = "触发奖励的连续天数，1 表示每日基础奖励")
    private Integer continuousDays;

    /**
     * 命中规则时奖励的积分数量。
     */
    @Schema(description = "奖励积分")
    private Integer integration;

    /**
     * 命中规则时奖励的成长值数量。
     */
    @Schema(description = "奖励成长值")
    private Integer growth;

    /**
     * 规则备注。
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 启用状态：0-禁用 1-启用。
     */
    @Schema(description = "启用状态：0-禁用 1-启用")
    private Integer enableStatus;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
