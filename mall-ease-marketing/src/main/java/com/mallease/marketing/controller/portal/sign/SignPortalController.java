package com.mallease.marketing.controller.portal.sign;

import com.mallease.common.api.R;
import com.mallease.marketing.controller.admin.sign.vo.SignRuleRespVO;
import com.mallease.marketing.controller.portal.sign.vo.SignCalendarRespVO;
import com.mallease.marketing.controller.portal.sign.vo.SignCheckInRespVO;
import com.mallease.marketing.controller.portal.sign.vo.SignInfoRespVO;
import com.mallease.marketing.convert.SignConvert;
import com.mallease.marketing.service.sign.SignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 签到前台控制器。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Tag(name = "签到前台")
@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/sign")
public class SignPortalController {

    private final SignService signService;
    private final SignConvert signConvert;

    @Operation(summary = "获取会员签到信息")
    @GetMapping("/info")
    public R<SignInfoRespVO> getInfo() {
        return R.success(signService.getInfo());
    }

    @Operation(summary = "获取会员签到日历")
    @GetMapping("/calendar")
    public R<SignCalendarRespVO> getCalendar(@RequestParam String yearMonth) {
        return R.success(signService.getCalendar(yearMonth));
    }

    @Operation(summary = "今日签到")
    @PostMapping("/check-in")
    public R<SignCheckInRespVO> checkIn() {
        return R.success(signService.checkIn());
    }

    @Operation(summary = "获取公开签到规则")
    @GetMapping("/rules")
    public R<List<SignRuleRespVO>> listRules() {
        return R.success(signConvert.toSignRuleRespList(signService.listEnabledRules()));
    }
}
