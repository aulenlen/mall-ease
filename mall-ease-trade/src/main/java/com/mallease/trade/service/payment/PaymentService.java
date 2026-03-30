package com.mallease.trade.service.payment;

import com.mallease.common.api.Page;
import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.controller.portal.payment.vo.PaymentCreateReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentPageReqVO;
import com.mallease.trade.dal.entity.PaymentOrder;

import java.util.List;
import java.util.Map;

/**
 * 支付服务。
 * 负责支付单创建、发起支付、支付状态查询、关闭支付单，以及处理第三方支付回调。
 */
public interface PaymentService {

    /**
     * 创建支付单。
     * 会基于订单号校验订单归属和订单状态，并按幂等规则返回已有支付单或新建支付单。
     *
     * @param userId 当前用户ID
     * @param reqVO  创建支付单请求
     * @return 支付单实体
     */
    PaymentOrder create(Long userId, PaymentCreateReqVO reqVO);

    /**
     * 执行支付
     *
     * @param userId 当前用户ID
     * @param paymentNo 支付单号
     * @param payChannel 支付渠道
     * @return 支付表单 HTML（支付宝 WAP），模拟支付返回 null
     */
    String pay(Long userId, String paymentNo, Integer payChannel);

    /**
     * 根据订单号查询当前用户的支付单。
     *
     * @param userId 当前用户ID
     * @param orderNo 订单号
     * @return 支付单，不存在时返回 null
     */
    PaymentOrder getByOrderNo(Long userId, String orderNo);

    /**
     * 根据支付单号查询支付单。
     *
     * @param paymentNo 支付单号
     * @return 支付单，不存在时返回 null
     */
    PaymentOrder getByPaymentNo(String paymentNo);

    /**
     * 管理端支付单分页列表。
     *
     * @param reqVO 分页查询条件
     * @return 支付单分页结果
     */
    Page<PaymentRespVO> pageAdminPayments(PaymentPageReqVO reqVO);

    /**
     * 管理端支付单详情。
     *
     * @param paymentNo 支付单号
     * @return 支付单详情
     */
    PaymentRespVO getAdminPaymentDetail(String paymentNo);

    /**
     * 关闭支付单。
     * 一般由订单取消或支付关闭链路触发。
     *
     * @param userId 当前用户ID
     * @param orderNo 订单号
     */
    void close(Long userId, String orderNo);

    /**
     * 处理支付宝异步回调。
     * 这里同时接收解析后的参数和回调原文：
     * 参数用于验签和业务处理，原文用于通知日志留痕、去重和排障。
     *
     * @param params 回调请求参数
     * @param rawBody 回调原文
     * @return true=处理成功
     */
    boolean handleAlipayNotify(Map<String, String> params, String rawBody);

}
