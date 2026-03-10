package com.mallease.trade.controller;

import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.trade.model.client.query.PaymentQuery;
import com.mallease.trade.model.client.vo.PaymentVO;
import com.mallease.trade.service.AdminPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端支付控制器
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Tag(name = "管理端-支付管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trade/admin/payment")
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @Operation(summary = "支付单分页列表", description = "支持多条件筛选")
    @GetMapping("/list")
    public R<Page<PaymentVO>> list(PaymentQuery query) {
        return R.success(adminPaymentService.list(query));
    }

    @Operation(summary = "支付单详情")
    @GetMapping("/detail")
    public R<PaymentVO> detail(@RequestParam String paymentNo) {
        return R.success(adminPaymentService.detail(paymentNo));
    }
}
