package com.mallease.user.dal.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员等级表
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
public class MemberLevel {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 等级名称
     */
    private String name;

    /**
     * 成长点门槛
     */
    private Integer growthPoint;

    /**
     * 是否为默认等级：0-否 1-是
     */
    private Integer defaultStatus;

    /**
     * 免运费标准（订单金额）
     */
    private BigDecimal freeFreightPoint;

    /**
     * 评价获取成长值
     */
    private Integer commentGrowthPoint;

    /**
     * 免邮特权：0-否 1-是
     */
    private Integer privilegeFreeFreight;

    /**
     * 签到特权：0-否 1-是
     */
    private Integer privilegeSignIn;

    /**
     * 评论获奖励特权：0-否 1-是
     */
    private Integer privilegeComment;

    /**
     * 专享活动特权：0-否 1-是
     */
    private Integer privilegePromotion;

    /**
     * 会员价格特权：0-否 1-是
     */
    private Integer privilegeMemberPrice;

    /**
     * 生日特权：0-否 1-是
     */
    private Integer privilegeBirthday;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
