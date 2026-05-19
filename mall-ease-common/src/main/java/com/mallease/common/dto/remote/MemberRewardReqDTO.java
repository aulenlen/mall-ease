package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会员积分/成长值变更请求。
 * <p>
 * 用于服务间调用 user 模块变更会员资产字段，并通过 businessType + businessKey 做幂等控制。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRewardReqDTO {

    /**
     * 积分变更量，可为负数。
     */
    private Integer integrationDelta;

    /**
     * 成长值变更量，可为负数。
     */
    private Integer growthDelta;

    /**
     * 业务类型，用于幂等去重。
     */
    private String businessType;

    /**
     * 业务幂等键，同一业务类型下唯一。
     */
    private String businessKey;

    /**
     * 变更原因。
     */
    private String reason;
}
