package com.mallease.marketing.controller.portal.sign.vo;

import com.mallease.marketing.controller.admin.sign.vo.SignRuleRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会员签到信息响应对象。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
@Schema(description = "会员签到信息")
public class SignInfoRespVO {

    /**
     * 今日是否已签到。
     */
    @Schema(description = "今日是否已签到")
    private Boolean todaySigned;

    /**
     * 当前连续签到天数。
     */
    @Schema(description = "当前连续签到天数")
    private Integer currentContinuousDays;

    /**
     * 下一个连续签到奖励里程碑。
     */
    @Schema(description = "下一个连续签到奖励里程碑")
    private SignRuleRespVO nextMilestone;
}
