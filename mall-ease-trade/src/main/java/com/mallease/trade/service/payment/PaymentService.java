package com.mallease.trade.service.payment;

import com.mallease.trade.controller.portal.payment.vo.PaymentCreateReqVO;
import com.mallease.trade.controller.admin.payment.vo.PaymentPageReqVO;
import com.mallease.trade.dal.entity.PaymentOrder;

import java.util.List;
import java.util.Map;

public interface PaymentService {

    PaymentOrder create(Long userId, PaymentCreateReqVO cmd);

    /**
     * 执行支付
     *
     * @return 支付表单 HTML（支付宝 WAP），模拟支付返回 null
     */
    String pay(Long userId, String paymentNo, Integer payChannel);

    PaymentOrder getByOrderNo(Long userId, String orderNo);

    PaymentOrder getByPaymentNo(String paymentNo);

    void close(Long userId, String orderNo);

    /**
     * 处理支付宝异步回调
     *
     * @param params 回调请求参数
     * @return true=处理成功
     */
    boolean handleAlipayNotify(Map<String, String> params);

    // ==================== 管理端方法 ====================

    /**
     * 根据订单编号查询支付单（无需用户ID，管理端使用）
     *
     * @param orderNo 订单编号
     * @return 支付单
     */
    PaymentOrder findByOrderNo(String orderNo);

    /**
     * 管理端支付单分页查询（多条件筛选）
     *
     * @param query 查询条件
     * @return 支付单列表（需配合 PageHelper 使用）
     */
    List<PaymentOrder> adminList(PaymentPageReqVO query);
}
