package com.mallease.trade.controller;

import com.mallease.common.api.R;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.converter.PaymentConverter;
import com.mallease.trade.model.client.cmd.PaymentCmd;
import com.mallease.trade.model.client.vo.PaymentVO;
import com.mallease.trade.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 *
 * @author: Aulen
 * @create: 2026-02-07
 */
@Slf4j
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
    public R<PaymentVO> pay(@RequestParam String paymentNo, @RequestParam Integer payChannel) {
        Long userId = LoginContextUtil.getUserId();
        String payForm = paymentService.pay(userId, paymentNo, payChannel);

        PaymentVO vo = paymentConverter.entityToVO(paymentService.getByPaymentNo(paymentNo));
        if (vo == null) {
            throw new ApiException("支付单不存在或不可支付");
        }
        vo.setPayForm(payForm);
        return R.success(vo);
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

    @Operation(summary = "支付宝异步回调", description = "支付宝服务器调用，无需登录")
    @PostMapping("/notify/alipay")
    public String alipayNotify(HttpServletRequest request) {

        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });

        log.info("收到支付宝回调: outTradeNo={}", params.get("out_trade_no"));

        boolean success = paymentService.handleAlipayNotify(params);

        return success ? "success" : "failure";
    }
}
