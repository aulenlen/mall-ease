package com.mallease.marketing.controller.admin.sign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 签到规则请求对象。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
@Schema(description = "签到规则请求对象")
public class SignRuleReqVO {

    /**
     * 创建规则校验分组。
     */
    public interface Create {}

    /**
     * 更新规则校验分组。
     */
    public interface Update {}

    /**
     * 主键ID，更新时必传。
     */
    @Schema(description = "主键ID（更新时必传）")
    @NotNull(groups = Update.class, message = "更新时主键ID不能为空")
    private Long id;

    /**
     * 触发奖励的连续签到天数，1 表示每日基础奖励。
     */
    @Schema(description = "触发奖励的连续天数，1 表示每日基础奖励")
    @NotNull(groups = Create.class, message = "连续天数不能为空")
    @Min(value = 1, message = "连续天数必须大于0")
    private Integer continuousDays;

    /**
     * 命中规则时奖励的积分数量。
     */
    @Schema(description = "奖励积分")
    @Min(value = 0, message = "奖励积分不能为负数")
    private Integer integration;

    /**
     * 命中规则时奖励的成长值数量。
     */
    @Schema(description = "奖励成长值")
    @Min(value = 0, message = "奖励成长值不能为负数")
    private Integer growth;

    /**
     * 规则备注，用于后台识别。
     */
    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;

    /**
     * 启用状态：0-禁用 1-启用。
     */
    @Schema(description = "启用状态：0-禁用 1-启用")
    @Min(value = 0, message = "启用状态必须为0或1")
    @Max(value = 1, message = "启用状态必须为0或1")
    private Integer enableStatus;
}
