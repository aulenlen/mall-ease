package com.mallease.marketing.controller.admin.sign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 签到记录响应对象。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
@Schema(description = "签到记录响应对象")
public class SignRecordRespVO {

    /**
     * 主键ID。
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 会员ID。
     */
    @Schema(description = "会员ID")
    private Long memberId;

    /**
     * 签到日期。
     */
    @Schema(description = "签到日期")
    private LocalDate signDate;

    /**
     * 签到后连续签到天数。
     */
    @Schema(description = "签到后连续签到天数")
    private Integer continuousDays;

    /**
     * 本次发放积分。
     */
    @Schema(description = "本次发放积分")
    private Integer integration;

    /**
     * 本次发放成长值。
     */
    @Schema(description = "本次发放成长值")
    private Integer growth;

    /**
     * 奖励状态：0-待处理 1-已处理 2-处理失败。
     */
    @Schema(description = "奖励状态：0-待处理 1-已处理 2-处理失败")
    private Integer rewardStatus;

    /**
     * 奖励幂等键。
     */
    @Schema(description = "奖励幂等键")
    private String rewardBizKey;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
