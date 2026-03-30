package com.mallease.trade.dal.mapper;

import com.mallease.trade.dal.entity.PaymentNotifyLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 支付回调日志 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-03-31
 */
@Mapper
public interface PaymentNotifyLogDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 支付回调日志
     */
    PaymentNotifyLog selectByPrimaryKey(@Param("id") Long id);

    /**
     * 插入支付回调日志
     *
     * @param record 支付回调日志
     * @return 影响行数
     */
    int insert(PaymentNotifyLog record);

    /**
     * 根据渠道和回调唯一ID查询
     *
     * @param channel 支付渠道
     * @param notifyId 渠道回调唯一ID
     * @return 支付回调日志
     */
    PaymentNotifyLog selectByChannelAndNotifyId(@Param("channel") Integer channel,
                                                @Param("notifyId") String notifyId);

    /**
     * 根据主键选择性更新
     *
     * @param record 支付回调日志
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(PaymentNotifyLog record);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(@Param("id") Long id);
}