package com.mallease.trade.dao;

import com.mallease.trade.model.data.entity.OrderShipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单物流发货 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Mapper
public interface OrderShipmentDao {

    /**
     * 插入物流记录
     *
     * @param record 物流记录
     * @return 影响行数
     */
    int insert(OrderShipment record);

    /**
     * 根据订单编号查询物流信息
     *
     * @param orderNo 订单编号
     * @return 物流信息
     */
    OrderShipment selectByOrderNo(@Param("orderNo") String orderNo);
}
