package com.mallease.marketing.controller.admin.sign.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 签到记录分页查询对象。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "签到记录分页查询对象")
public class SignRecordPageReqVO extends BaseQuery {

    /**
     * 会员ID。
     */
    @Schema(description = "会员ID")
    private Long memberId;

    /**
     * 签到日期下限。
     */
    @Schema(description = "签到日期下限")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFrom;

    /**
     * 签到日期上限。
     */
    @Schema(description = "签到日期上限")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTo;

    /**
     * 奖励状态：0-待处理 1-已处理 2-处理失败。
     */
    @Schema(description = "奖励状态：0-待处理 1-已处理 2-处理失败")
    private Integer rewardStatus;
}
