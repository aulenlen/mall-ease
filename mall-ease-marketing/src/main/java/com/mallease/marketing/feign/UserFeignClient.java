package com.mallease.marketing.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.MemberDTO;
import com.mallease.common.dto.remote.MemberRewardReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户服务 Feign 客户端。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@FeignClient(name = "mall-ease-user")
public interface UserFeignClient {

    /**
     * 变更会员积分/成长值。
     *
     * @param id 会员ID
     * @param reqDTO 奖励变更请求
     * @return 更新后的会员信息
     */
    @PostMapping("/user/member/internal/{id}/rewards")
    R<MemberDTO> addMemberRewards(@PathVariable("id") Long id, @RequestBody MemberRewardReqDTO reqDTO);
}
