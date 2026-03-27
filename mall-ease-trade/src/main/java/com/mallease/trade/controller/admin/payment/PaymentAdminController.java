package com.mallease.trade.controller.admin.payment;

import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.trade.controller.admin.payment.vo.PaymentPageReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.service.payment.PaymentService;
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
public class PaymentAdminController {

    private final PaymentService paymentService;

    @Operation(summary = "支付单分页列表", description = "支持多条件筛选")
    @GetMapping("/list")
    public R<Page<PaymentRespVO>> list(PaymentPageReqVO reqVO) {
        return R.success(paymentService.pageAdminPayments(reqVO));
    }

    @Operation(summary = "支付单详情")
    @GetMapping("/detail")
    public R<PaymentRespVO> detail(@RequestParam String paymentNo) {
        return R.success(paymentService.getAdminPaymentDetail(paymentNo));
    }
}
