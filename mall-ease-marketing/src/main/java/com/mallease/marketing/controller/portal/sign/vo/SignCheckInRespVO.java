package com.mallease.marketing.controller.portal.sign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 签到结果响应对象。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
@Schema(description = "签到结果")
public class SignCheckInRespVO {

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

}
