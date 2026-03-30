package com.mallease.trade.service.payment;

import cn.hutool.crypto.digest.DigestUtil;
import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.enums.PayChannel;
import com.mallease.common.enums.PaymentStatus;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.NoGeneratorUtil;
import com.mallease.trade.constant.PaymentNotifyProcessStatus;
import com.mallease.trade.controller.admin.payment.vo.PaymentPageReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.controller.portal.payment.vo.PaymentCreateReqVO;
import com.mallease.trade.convert.payment.PaymentConvert;
import com.mallease.trade.dal.entity.PaymentNotifyLog;
import com.mallease.trade.dal.mapper.PaymentOrderDao;
import com.mallease.trade.dal.mapper.PaymentNotifyLogDao;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.PaymentOrder;
import com.mallease.trade.service.order.OrderService;
import com.mallease.trade.service.payment.handler.AlipayHandler;
import com.mallease.trade.service.payment.handler.PayChannelHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 支付服务实现
 *
 * @author: Aulen
 * @create: 2026-02-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final int SIGN_NOT_VERIFIED = 0;
    private static final int SIGN_VERIFIED = 1;

    private final TransactionTemplate transactionTemplate;
    private final PaymentOrderDao paymentOrderDao;
    private final PaymentNotifyLogDao paymentNotifyLogDao;
    private final OrderService orderService;
    private final AlipayHandler alipayHandler;
    private final PaymentConvert paymentConvert;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrder create(Long userId, PaymentCreateReqVO reqVO) {

        Order pendingOrder = orderService.findPendingPaymentOrder(userId, reqVO.getOrderNo());

        if (pendingOrder == null) {
            throw new ApiException("订单错误");
        }
        PaymentOrder existPayment = paymentOrderDao.selectByOrderNo(reqVO.getOrderNo());

        if (existPayment == null) {
            PaymentOrder payment = PaymentOrder.builder()
                    .paymentNo(NoGeneratorUtil.generate(userId))
                    .orderNo(reqVO.getOrderNo())
                    .userId(userId)
                    .payAmount(pendingOrder.getPayAmount())
                    .status(PaymentStatus.PENDING.getCode())
                    .expireTime(LocalDateTime.now().plusMinutes(30))
                    .build();

            paymentOrderDao.insert(payment);
            return paymentOrderDao.selectByOrderNo(reqVO.getOrderNo());
        }

        if (existPayment.getStatus().equals(PaymentStatus.PENDING.getCode())) {
            return existPayment;
        }

        if (existPayment.getStatus().equals(PaymentStatus.FAILED.getCode())) {
            paymentOrderDao.resetForRetry(
                    existPayment.getId(),
                    NoGeneratorUtil.generate(userId),
                    LocalDateTime.now().plusMinutes(30)
            );
            return paymentOrderDao.selectByOrderNo(reqVO.getOrderNo());
        }

        if (existPayment.getStatus().equals(PaymentStatus.SUCCESS.getCode())) {
            throw new ApiException("订单已支付");
        }

        if (existPayment.getStatus().equals(PaymentStatus.CLOSED.getCode())) {
            throw new ApiException("订单已关闭");
        }

        return null;
    }

    @Override
    public String pay(Long userId, String paymentNo, Integer payChannel) {
        PaymentOrder payment = paymentOrderDao.findPending(userId, paymentNo);
        if (payment == null) {
            throw new ApiException("支付单不存在或已过期");
        }

        if (PayChannel.MOCK.getCode().equals(payChannel)) {
            Integer rows = transactionTemplate.execute(status ->
                    paymentOrderDao.updateStatus(
                            payment.getId(),
                            PaymentStatus.SUCCESS.getCode(),
                            payChannel,
                            LocalDateTime.now(),
                            "mock"
                    )
            );
            if (rows == null || rows == 0) {
                log.info("模拟支付单状态已变更，跳过本次处理: paymentNo={}", payment.getPaymentNo());
                return null;
            }
            try {
                orderService.confirmPaidOrder(payment.getOrderNo());
            } catch (ApiException ex) {
                log.warn("模拟支付成功后订单推进未完成，orderNo={}, message={}", payment.getOrderNo(), ex.getMessage());
                throw new ApiException("支付成功，订单处理中");
            }
            return null;
        }

        PayChannelHandler handler = getHandler(payChannel);
        return handler.prepay(payment);
    }

    @Override
    public PaymentOrder getByOrderNo(Long userId, String orderNo) {
        return paymentOrderDao.selectByUserIdAndOrderNo(userId, orderNo);
    }

    @Override
    public PaymentOrder getByPaymentNo(String paymentNo) {
        return paymentOrderDao.selectByPaymentNo(paymentNo);
    }

    @Override
    public Page<PaymentRespVO> pageAdminPayments(PaymentPageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<PaymentOrder> payments = paymentOrderDao.adminList(reqVO);
        if (payments.isEmpty()) {
            return PageUtils.buildPage(payments, Collections.emptyList());
        }
        List<PaymentRespVO> voList = payments.stream()
                .map(paymentConvert::toPaymentResp)
                .toList();
        return PageUtils.buildPage(payments, voList);
    }

    @Override
    public PaymentRespVO getAdminPaymentDetail(String paymentNo) {
        PaymentOrder payment = getByPaymentNo(paymentNo);
        if (payment == null) {
            throw new ApiException("支付单不存在");
        }
        return paymentConvert.toPaymentResp(payment);
    }

    @Override
    public void close(Long userId, String orderNo) {
        paymentOrderDao.closeByOrderNo(userId, orderNo, PaymentStatus.CLOSED.getCode());
    }

    @Override
    public boolean handleAlipayNotify(Map<String, String> params, String rawBody) {
        String normalizedRawBody = rawBody == null ? "" : rawBody;
        PaymentNotifyLog notifyLog = initAlipayNotifyLog(params, normalizedRawBody);
        if (notifyLog != null && isNotifyLogFinalStatus(notifyLog.getProcessStatus())) {
            log.info("支付宝回调已处理，忽略重复通知: notifyId={}", notifyLog.getNotifyId());
            return true;
        }

        Map<String, String> notifyMap = alipayHandler.handleNotify(params);

        if (notifyMap == null || notifyMap.isEmpty()) {
            updateNotifyLogResult(notifyLog, null, null, null, SIGN_NOT_VERIFIED, PaymentNotifyProcessStatus.FAILED.getCode(), "支付宝回调验签失败");
            log.warn("支付宝回调验签失败");
            return false;
        }

        String outTradeNo = notifyMap.get("out_trade_no");
        String tradeNo = notifyMap.get("trade_no");
        String tradeStatus = notifyMap.get("trade_status");
        String buyerId = notifyMap.get("buyer_id");
        String gmtPayment = notifyMap.get("gmt_payment");
        updateNotifyLogResult(notifyLog, outTradeNo, null, tradeNo, SIGN_VERIFIED, PaymentNotifyProcessStatus.PENDING.getCode(), null);

        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            updateNotifyLogResult(notifyLog, outTradeNo, null, tradeNo, SIGN_VERIFIED, PaymentNotifyProcessStatus.DUPLICATE.getCode(), "忽略非成功交易状态:" + tradeStatus);
            log.info("支付宝回调失败: tradeStatus={}", tradeStatus);
            return true;
        }

        PaymentOrder payment = paymentOrderDao.selectByPaymentNo(outTradeNo);
        if (payment == null) {
            updateNotifyLogResult(notifyLog, outTradeNo, null, tradeNo, SIGN_VERIFIED, PaymentNotifyProcessStatus.FAILED.getCode(), "回调对应的支付单不存在");
            log.warn("回调对应的支付单不存在: outTradeNo={}", outTradeNo);
            return false;
        }

        if (PaymentStatus.SUCCESS.getCode().equals(payment.getStatus())) {
            updateNotifyLogResult(notifyLog, payment.getPaymentNo(), payment.getOrderNo(), tradeNo, SIGN_VERIFIED, PaymentNotifyProcessStatus.DUPLICATE.getCode(), "支付单已处理，忽略重复回调");
            log.info("支付单已处理，忽略重复回调: paymentNo={}", payment.getPaymentNo());
            return true;
        }

        LocalDateTime paidTime = parsePaidTime(gmtPayment);

        Integer rows = transactionTemplate.execute(status -> {
            int updated = paymentOrderDao.updateStatus(payment.getId(), PaymentStatus.SUCCESS.getCode(), PayChannel.ALIPAY.getCode(), paidTime, tradeNo);
            if (updated > 0) {
                PaymentOrder notifyUpdate = new PaymentOrder();
                notifyUpdate.setId(payment.getId());
                notifyUpdate.setThirdBuyerId(buyerId);
                notifyUpdate.setNotifyTime(LocalDateTime.now());
                notifyUpdate.setNotifyCount(payment.getNotifyCount() == null ? 1 : payment.getNotifyCount() + 1);
                paymentOrderDao.updateByPrimaryKeySelective(notifyUpdate);
            }
            return updated;
        });

        if (rows == null || rows == 0) {
            updateNotifyLogResult(notifyLog, payment.getPaymentNo(), payment.getOrderNo(), tradeNo, SIGN_VERIFIED, PaymentNotifyProcessStatus.DUPLICATE.getCode(), "支付单状态已变更，跳过本次回调");
            log.info("支付单状态已变更，跳过本次回调: paymentNo={}", payment.getPaymentNo());
            return true;
        }

        try {
            orderService.confirmPaidOrder(payment.getOrderNo());
        } catch (ApiException ex) {
            updateNotifyLogResult(
                    notifyLog,
                    payment.getPaymentNo(),
                    payment.getOrderNo(),
                    tradeNo,
                    SIGN_VERIFIED,
                    PaymentNotifyProcessStatus.FAILED.getCode(),
                    ex.getMessage()
            );
            log.warn("支付回调后确认库存未完成，orderNo={}, message={}", payment.getOrderNo(), ex.getMessage());
            return true;
        }

        updateNotifyLogResult(
                notifyLog,
                payment.getPaymentNo(),
                payment.getOrderNo(),
                tradeNo,
                SIGN_VERIFIED,
                PaymentNotifyProcessStatus.SUCCESS.getCode(),
                null
        );
        log.info("支付宝回调处理成功: paymentNo={}, tradeNo={}, buyerId={}", payment.getPaymentNo(), tradeNo, buyerId);

        return true;
    }

    /**
     * 解析支付宝回传的付款时间
     */
    private LocalDateTime parsePaidTime(String gmtPayment) {
        if (gmtPayment == null || gmtPayment.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(gmtPayment, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("解析支付宝付款时间失败: {}, 使用当前时间", gmtPayment);
            return LocalDateTime.now();
        }
    }

    private PaymentNotifyLog initAlipayNotifyLog(Map<String, String> params, String rawBody) {
        String notifyId = resolveNotifyId(params, rawBody);
        PaymentNotifyLog existing = paymentNotifyLogDao.selectByChannelAndNotifyId(PayChannel.ALIPAY.getCode(), notifyId);
        if (existing != null) {
            return existing;
        }

        PaymentNotifyLog notifyLog = PaymentNotifyLog.builder()
                .channel(PayChannel.ALIPAY.getCode())
                .notifyId(notifyId)
                .paymentNo(params == null ? null : params.get("out_trade_no"))
                .thirdTradeNo(params == null ? null : params.get("trade_no"))
                .signVerified(SIGN_NOT_VERIFIED)
                .processStatus(PaymentNotifyProcessStatus.PENDING.getCode())
                .rawBody(rawBody)
                .notifyTime(LocalDateTime.now())
                .build();
        try {
            paymentNotifyLogDao.insert(notifyLog);
            return notifyLog;
        } catch (DuplicateKeyException ex) {
            return paymentNotifyLogDao.selectByChannelAndNotifyId(PayChannel.ALIPAY.getCode(), notifyId);
        }
    }

    private String resolveNotifyId(Map<String, String> params, String rawBody) {
        if (params != null) {
            String notifyId = params.get("notify_id");
            if (notifyId != null && !notifyId.isBlank()) {
                return notifyId;
            }
            String tradeNo = params.get("trade_no");
            if (tradeNo != null && !tradeNo.isBlank()) {
                return "trade:" + tradeNo;
            }
            String outTradeNo = params.get("out_trade_no");
            if (outTradeNo != null && !outTradeNo.isBlank()) {
                return "payment:" + outTradeNo + ":" + DigestUtil.sha256Hex(rawBody);
            }
        }
        return "payload:" + DigestUtil.sha256Hex(rawBody);
    }

    private boolean isNotifyLogFinalStatus(Integer processStatus) {
        return Objects.equals(processStatus, PaymentNotifyProcessStatus.SUCCESS.getCode())
                || Objects.equals(processStatus, PaymentNotifyProcessStatus.DUPLICATE.getCode());
    }

    private void updateNotifyLogResult(PaymentNotifyLog notifyLog,
                                       String paymentNo,
                                       String orderNo,
                                       String thirdTradeNo,
                                       Integer signVerified,
                                       Integer processStatus,
                                       String errorMsg) {
        if (notifyLog == null || notifyLog.getId() == null) {
            return;
        }
        PaymentNotifyLog update = PaymentNotifyLog.builder()
                .id(notifyLog.getId())
                .paymentNo(paymentNo)
                .orderNo(orderNo)
                .thirdTradeNo(thirdTradeNo)
                .signVerified(signVerified)
                .processStatus(processStatus)
                .errorMsg(errorMsg)
                .notifyTime(LocalDateTime.now())
                .build();
        paymentNotifyLogDao.updateByPrimaryKeySelective(update);
        notifyLog.setPaymentNo(paymentNo);
        notifyLog.setOrderNo(orderNo);
        notifyLog.setThirdTradeNo(thirdTradeNo);
        notifyLog.setSignVerified(signVerified);
        notifyLog.setProcessStatus(processStatus);
        notifyLog.setErrorMsg(errorMsg);
        notifyLog.setNotifyTime(update.getNotifyTime());
    }

    /**
     * 根据支付渠道获取对应的 Handler
     */
    private PayChannelHandler getHandler(Integer payChannel) {
        PayChannel channel = PayChannel.of(payChannel);
        if (channel == null) {
            throw new ApiException("不支持的支付渠道: " + payChannel);
        }
        return switch (channel) {
            case ALIPAY -> alipayHandler;
            default -> throw new ApiException("不支持的支付渠道: " + channel.getDesc());
        };
    }

}
