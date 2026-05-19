package com.mallease.marketing.controller.portal.sign.vo;

import com.mallease.marketing.controller.admin.sign.vo.SignRuleRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 会员签到日历响应对象。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
@Schema(description = "会员签到日历")
public class SignCalendarRespVO {

    /**
     * 日历月份，格式 yyyy-MM。
     */
    @Schema(description = "日历月份，格式 yyyy-MM")
    private String yearMonth;

    /**
     * 当月签到日期列表。
     */
    @Schema(description = "当月签到日期列表")
    private List<Day> days;

    /**
     * 当前启用的签到规则预览。
     */
    @Schema(description = "当前启用的签到规则预览")
    private List<SignRuleRespVO> rules;

    /**
     * 日历中的单日签到状态。
     */
    @Data
    @Schema(description = "签到日历日期")
    public static class Day {

        /**
         * 签到日期。
         */
        @Schema(description = "签到日期")
        private LocalDate date;

        /**
         * 当天记录的连续签到天数。
         */
        @Schema(description = "当天记录的连续签到天数")
        private Integer continuousDays;

        /**
         * 奖励状态：0-待处理 1-已处理 2-处理失败。
         */
        @Schema(description = "奖励状态：0-待处理 1-已处理 2-处理失败")
        private Integer rewardStatus;
    }
}
