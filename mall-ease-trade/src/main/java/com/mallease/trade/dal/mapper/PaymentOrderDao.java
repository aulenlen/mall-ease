package com.mallease.trade.dal.mapper;

import com.mallease.trade.controller.admin.payment.vo.PaymentPageReqVO;
import com.mallease.trade.dal.entity.PaymentOrder;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付单 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-02-06
 */
@Mapper
public interface PaymentOrderDao {


    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 支付单
     */
    PaymentOrder selectByPrimaryKey(@Param("id") Long id);

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
     * 根据用户ID和订单号查询支付单
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @return 支付单
     */
    PaymentOrder selectByUserIdAndOrderNo(@Param("userId") Long userId,
                                          @Param("orderNo") String orderNo);

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
                     @Param("payChannel") Integer payChannel,
                     @Param("paidTime") LocalDateTime paidTime,
                     @Param("thirdTradeNo") String thirdTradeNo);

    /**
     * 根据订单号关闭支付单（订单取消时联动）
     *
     * @param orderNo 订单号
     * @param status  关闭状态
     * @return 影响行数
     */
    int closeByOrderNo(@Param("userId") Long userId,
                       @Param("orderNo") String orderNo,
                       @Param("status") Integer status);

    /**
     * 批量根据订单号关闭支付单（订单取消时联动）
     *
     * @param orderNos 订单号列表
     * @param status   关闭状态
     * @return 影响行数
     */
    int closeByOrderNos(@Param("orderNos") List<String> orderNos,
                        @Param("status") Integer status);

    /**
     * 重置失败支付单为待支付
     *
     * @param id         主键ID
     * @param paymentNo  新支付单号
     * @param expireTime 过期时间
     * @return 影响行数
     */
    int resetForRetry(@Param("id") Long id,
                      @Param("paymentNo") String paymentNo,
                      @Param("expireTime") LocalDateTime expireTime);

    PaymentOrder findPending(@Param("userId") Long userId, @Param("paymentNo") String paymentNo);

    /**
     * 根据主键选择性更新（只更新非null字段）
     *
     * @param record 支付单
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PaymentOrder record);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(@Param("id") Long id);

    /**
     * 管理端支付单分页查询
     *
     * @param query 查询条件
     * @return 支付单列表
     */
    List<PaymentOrder> adminList(PaymentPageReqVO query);
}
