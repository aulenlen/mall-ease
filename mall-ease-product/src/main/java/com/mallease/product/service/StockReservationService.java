package com.mallease.product.service;

import com.mallease.product.model.data.entity.StockReservation;

import java.util.List;

public interface StockReservationService {

    /**
     * 查询已过期的锁定记录（用于定时任务释放库存）
     *
     * @param limit  每次处理的最大数量
     * @return 已过期的预占记录列表
     */
    List<StockReservation> listExpiredLocked(Integer limit);

    /**
     * 查询指定订单下处于 LOCKED 状态的预占记录
     *
     * @param orderNo 订单编号
     * @return 已锁定的预占记录列表
     */
    List<StockReservation> listLockedByOrderNo(String orderNo);

    /**
     * 按订单号批量更新状态（释放/确认时使用）
     *
     * @param orderNo   订单编号
     * @param oldStatus 原状态（防止重复操作）
     * @param newStatus 新状态
     * @return 影响行数
     */
    int updateStatusByOrderNo(String orderNo, Integer oldStatus, Integer newStatus);

    /**
     * 按预占记录ID批量更新状态为已释放
     *
     * @param ids 预占记录ID列表
     * @return 影响行数
     */
    int updateStatusToReleasedByIds(List<Long> ids);

    /**
     * 按订单号查询预约记录
     */
    List<StockReservation> listByOrderNo(String orderNo);

    /**
     * 批量插入预占记录
     */
    int insertBatch(List<StockReservation> stockReservations);

    /**
     * 查询已锁定的预占记录
     *
     * @param orderNos 订单编号列表
     * @return 已锁定的预占记录列表
     */
    List<StockReservation> listLockedByOrderNos(List<String> orderNos);
}
