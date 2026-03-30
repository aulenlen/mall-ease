package com.mallease.trade.controller.portal.payment;

import com.mallease.common.api.R;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.convert.payment.PaymentConvert;
import com.mallease.trade.controller.portal.payment.vo.PaymentCreateReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.service.payment.PaymentService;
import com.mallease.trade.service.payment.handler.AlipayNotifyRequestParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/portal/payments")
public class PaymentPortalController {

    private final PaymentService paymentService;
    private final PaymentConvert paymentConverter;
    private final AlipayNotifyRequestParser alipayNotifyRequestParser;

    @Operation(summary = "创建支付单")
    @PostMapping
    public R<PaymentRespVO> create(@Validated @RequestBody PaymentCreateReqVO reqVO) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(paymentConverter.toPaymentResp(paymentService.create(userId, reqVO)));
    }

    @Operation(summary = "执行支付")
    @PostMapping("/{paymentNo}/pay")
    public R<PaymentRespVO> pay(@PathVariable String paymentNo, @RequestParam Integer payChannel) {
        Long userId = LoginContextUtil.getUserId();
        String payForm = paymentService.pay(userId, paymentNo, payChannel);

        PaymentRespVO vo = paymentConverter.toPaymentResp(paymentService.getByPaymentNo(paymentNo));
        if (vo == null) {
            throw new ApiException("支付单不存在或不可支付");
        }
        vo.setPayForm(payForm);
        return R.success(vo);
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/orders/{orderNo}/status")
    public R<PaymentRespVO> status(@PathVariable String orderNo) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(paymentConverter.toPaymentResp(paymentService.getByOrderNo(userId, orderNo)));
    }

    @Operation(summary = "关闭支付单")
    @PostMapping("/orders/{orderNo}/close")
    public R<Void> close(@PathVariable String orderNo) {
        Long userId = LoginContextUtil.getUserId();
        paymentService.close(userId, orderNo);
        return R.success(null);
    }

    @Operation(summary = "支付宝异步回调", description = "支付宝服务器调用，无需登录")
    @PostMapping("/notify/alipay")
    public String alipayNotify(HttpServletRequest request) {
        AlipayNotifyRequestParser.ParsedRequest parsedRequest = alipayNotifyRequestParser.parse(request);

        log.info("收到支付宝回调: outTradeNo={}", parsedRequest.params().get("out_trade_no"));

        boolean success = paymentService.handleAlipayNotify(parsedRequest.params(), parsedRequest.rawBody());

        return success ? "success" : "failure";
    }
}
