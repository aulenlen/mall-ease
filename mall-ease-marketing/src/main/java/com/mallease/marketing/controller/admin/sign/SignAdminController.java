package com.mallease.marketing.controller.admin.sign;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.marketing.controller.admin.sign.vo.SignRecordPageReqVO;
import com.mallease.marketing.controller.admin.sign.vo.SignRecordRespVO;
import com.mallease.marketing.controller.admin.sign.vo.SignRuleReqVO;
import com.mallease.marketing.controller.admin.sign.vo.SignRuleRespVO;
import com.mallease.marketing.convert.SignConvert;
import com.mallease.marketing.dal.entity.SignRecord;
import com.mallease.marketing.dal.entity.SignRule;
import com.mallease.marketing.service.sign.SignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 签到后台管理控制器。
 *
 * @author: Aulen
 * @create: 2026-05-06
 */
@Tag(name = "签到后台管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sign")
public class SignAdminController {

    private final SignService signService;
    private final SignConvert signConvert;

    @Operation(summary = "创建签到规则")
    @PostMapping("/rules")
    public R<Long> createRule(@Validated(SignRuleReqVO.Create.class) @RequestBody SignRuleReqVO reqVO) {
        SignRule rule = signConvert.toSignRule(reqVO);
        return R.success(signService.createRule(rule));
    }

    @Operation(summary = "更新签到规则")
    @PutMapping("/rules")
    public R<Integer> updateRule(@Validated(SignRuleReqVO.Update.class) @RequestBody SignRuleReqVO reqVO) {
        SignRule rule = signConvert.toSignRule(reqVO);
        return R.success(signService.updateRule(rule));
    }

    @Operation(summary = "获取签到规则详情")
    @GetMapping("/rules/{id:\\d+}")
    public R<SignRuleRespVO> getRule(@Parameter(description = "规则ID", required = true) @PathVariable Long id) {
        return R.success(signConvert.toSignRuleResp(signService.getRule(id)));
    }

    @Operation(summary = "删除签到规则")
    @DeleteMapping("/rules/{id:\\d+}")
    public R<Integer> deleteRule(@Parameter(description = "规则ID", required = true) @PathVariable Long id) {
        return R.success(signService.deleteRule(id));
    }

    @Operation(summary = "查询签到规则列表")
    @GetMapping("/rules")
    public R<List<SignRuleRespVO>> listRules() {
        return R.success(signConvert.toSignRuleRespList(signService.listRules()));
    }

    @Operation(summary = "分页查询签到记录")
    @GetMapping("/records")
    public R<Page<SignRecordRespVO>> listRecords(@ParameterObject SignRecordPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<SignRecord> records = signService.pageRecords(reqVO);
        return R.success(PageUtils.convertPage(records, signConvert::toSignRecordRespList));
    }
}
