package com.mallease.user.controller.admin.memberlevel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员等级返回
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员等级返回")
public class MemberLevelRespVO {

    @Schema(description = "会员等级ID")
    private Long id;

    @Schema(description = "等级名称")
    private String name;

    @Schema(description = "成长点门槛")
    private Integer growthPoint;

    @Schema(description = "是否为默认等级：0-否 1-是")
    private Integer defaultStatus;

    @Schema(description = "免运费标准（订单金额）")
    private BigDecimal freeFreightPoint;

    @Schema(description = "评价获取成长值")
    private Integer commentGrowthPoint;

    @Schema(description = "免邮特权：0-否 1-是")
    private Integer privilegeFreeFreight;

    @Schema(description = "签到特权：0-否 1-是")
    private Integer privilegeSignIn;

    @Schema(description = "评论获奖励特权：0-否 1-是")
    private Integer privilegeComment;

    @Schema(description = "专享活动特权：0-否 1-是")
    private Integer privilegePromotion;

    @Schema(description = "会员价格特权：0-否 1-是")
    private Integer privilegeMemberPrice;

    @Schema(description = "生日特权：0-否 1-是")
    private Integer privilegeBirthday;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}