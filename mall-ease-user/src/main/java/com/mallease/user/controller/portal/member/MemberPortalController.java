package com.mallease.user.controller.portal.member;

import com.mallease.common.api.R;
import com.mallease.user.config.StpMemberUtil;
import com.mallease.user.convert.MemberConvert;
import com.mallease.user.controller.portal.member.vo.MemberRespVO;
import com.mallease.user.dal.entity.Member;
import com.mallease.user.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台会员控制器。
 *
 * @author: Aulen
 * @create: 2026-03-27
 */
@Tag(name = "前台会员中心", description = "会员个人信息查询")
@RestController
@RequestMapping("/user/member/portal")
@RequiredArgsConstructor
public class MemberPortalController {

    private final UserService userService;
    private final MemberConvert memberConvert;

    @Operation(summary = "获取当前登录会员信息", description = "前台接口，返回会员基本信息")
    @GetMapping("/me")
    public R<MemberRespVO> getCurrentMember() {
        Long memberId = StpMemberUtil.getLoginIdAsLong();
        Member member = userService.getMemberById(memberId);
        return R.success(memberConvert.toMemberResp(member));
    }
}
