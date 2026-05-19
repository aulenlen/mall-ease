package com.mallease.user.dal.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员奖励幂等流水。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
public class MemberRewardLog {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务幂等键
     */
    private String businessKey;

    /**
     * 积分变更量
     */
    private Integer integrationDelta;

    /**
     * 成长值变更量
     */
    private Integer growthDelta;

    /**
     * 变更原因
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
