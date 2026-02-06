package com.mallease.trade.dao;

import com.mallease.trade.model.data.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 支付单 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-02-06
 */
@Mapper
public interface PaymentOrderDao {

    /**
     * 插入支付单
     *
     * @param record 支付单
     * @return 影响行数
     */
    int insert(PaymentOrder record);

    /**
     * 根据支付单号查询
     *
     * @param paymentNo 支付单号
     * @return 支付单
     */
    PaymentOrder selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据订单号查询
     *
     * @param orderNo 订单号
     * @return 支付单
     */
    PaymentOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 更新支付状态（支付成功/失败时调用）
     *
     * @param id           主键ID
     * @param status       目标状态
     * @param paidTime     支付成功时间
     * @param thirdTradeNo 第三方交易号
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("paidTime") LocalDateTime paidTime,
                     @Param("thirdTradeNo") String thirdTradeNo);

    /**
     * 根据订单号关闭支付单（订单取消时联动）
     *
     * @param orderNo 订单号
     * @param status  关闭状态
     * @return 影响行数
     */
    int closeByOrderNo(@Param("orderNo") String orderNo,
                       @Param("status") Integer status);
}
