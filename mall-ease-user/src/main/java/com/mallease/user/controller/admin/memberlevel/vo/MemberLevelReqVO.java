package com.mallease.user.controller.admin.memberlevel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 会员等级保存请求
 *
 * @author: Aulen
 * @create: 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员等级保存请求")
public class MemberLevelReqVO {

    /**
     * 创建时的校验组
     */
    public interface Create {
    }

    /**
     * 更新时的校验组
     */
    public interface Update {
    }

    @Schema(description = "会员等级ID（更新时必填）")
    @NotNull(groups = Update.class, message = "会员等级ID不能为空")
    private Long id;

    @Schema(description = "等级名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "等级名称不能为空")
    @Size(max = 100, message = "等级名称长度不能超过100个字符")
    private String name;

    @Schema(description = "成长点门槛")
    @PositiveOrZero(message = "成长点门槛不能为负数")
    private Integer growthPoint;

    @Schema(description = "是否为默认等级：0-否 1-是")
    @Min(value = 0, message = "默认状态值必须为0或1")
    @Max(value = 1, message = "默认状态值必须为0或1")
    private Integer defaultStatus;

    @Schema(description = "免运费标准（订单金额）")
    @PositiveOrZero(message = "免运费标准不能为负数")
    private BigDecimal freeFreightPoint;

    @Schema(description = "评价获取成长值")
    @PositiveOrZero(message = "评价获取成长值不能为负数")
    private Integer commentGrowthPoint;

    @Schema(description = "免邮特权：0-否 1-是")
    @Min(value = 0, message = "免邮特权值必须为0或1")
    @Max(value = 1, message = "免邮特权值必须为0或1")
    private Integer privilegeFreeFreight;

    @Schema(description = "签到特权：0-否 1-是")
    @Min(value = 0, message = "签到特权值必须为0或1")
    @Max(value = 1, message = "签到特权值必须为0或1")
    private Integer privilegeSignIn;

    @Schema(description = "评论获奖励特权：0-否 1-是")
    @Min(value = 0, message = "评论奖励特权值必须为0或1")
    @Max(value = 1, message = "评论奖励特权值必须为0或1")
    private Integer privilegeComment;

    @Schema(description = "专享活动特权：0-否 1-是")
    @Min(value = 0, message = "专享活动特权值必须为0或1")
    @Max(value = 1, message = "专享活动特权值必须为0或1")
    private Integer privilegePromotion;

    @Schema(description = "会员价格特权：0-否 1-是")
    @Min(value = 0, message = "会员价格特权值必须为0或1")
    @Max(value = 1, message = "会员价格特权值必须为0或1")
    private Integer privilegeMemberPrice;

    @Schema(description = "生日特权：0-否 1-是")
    @Min(value = 0, message = "生日特权值必须为0或1")
    @Max(value = 1, message = "生日特权值必须为0或1")
    private Integer privilegeBirthday;

    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String note;
}