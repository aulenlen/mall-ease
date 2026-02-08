package com.mallease.trade.controller;

import com.mallease.common.api.R;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.converter.PaymentConverter;
import com.mallease.trade.model.client.cmd.PaymentCmd;
import com.mallease.trade.model.client.vo.PaymentVO;
import com.mallease.trade.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 *
 * @author: Aulen
 * @create: 2026-02-07
 */
@Tag(name = "支付管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trade/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentConverter paymentConverter;

    @Operation(summary = "创建支付单")
    @PostMapping("/portal/create")
    public R<PaymentVO> create(@Validated @RequestBody PaymentCmd cmd) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(paymentConverter.entityToVO(paymentService.create(userId, cmd)));
    }

    @Operation(summary = "执行支付")
    @PostMapping("/portal/pay")
    public R<PaymentVO> pay(@RequestParam String paymentNo,
                            @RequestParam Integer payChannel) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(paymentConverter.entityToVO(paymentService.pay(userId, paymentNo, payChannel)));
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/portal/status")
    public R<PaymentVO> status(@RequestParam String orderNo) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(paymentConverter.entityToVO(paymentService.getByOrderNo(userId, orderNo)));
    }

    @Operation(summary = "关闭支付单")
    @PostMapping("/portal/close")
    public R<Void> close(@RequestParam String orderNo) {
        Long userId = LoginContextUtil.getUserId();
        paymentService.close(userId, orderNo);
        return R.success(null);
    }
}
