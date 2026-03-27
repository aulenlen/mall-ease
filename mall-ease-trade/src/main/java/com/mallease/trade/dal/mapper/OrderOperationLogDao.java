package com.mallease.trade.dal.mapper;

import com.mallease.trade.dal.entity.OrderOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单操作日志 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Mapper
public interface OrderOperationLogDao {

    /**
     * 插入操作日志
     *
     * @param record 操作日志
     * @return 影响行数
     */
    int insert(OrderOperationLog record);

    /**
     * 根据订单编号查询操作日志
     *
     * @param orderNo 订单编号
     * @return 操作日志列表
     */
    List<OrderOperationLog> selectByOrderNo(@Param("orderNo") String orderNo);
}
