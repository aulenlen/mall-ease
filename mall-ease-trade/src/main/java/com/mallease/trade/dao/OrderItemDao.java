package com.mallease.trade.dao;

import com.mallease.trade.model.data.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单商品 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
@Mapper
public interface OrderItemDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 订单商品
     */
    OrderItem selectByPrimaryKey(Long id);

    /**
     * 根据订单ID查询商品列表
     *
     * @param orderId 订单ID
     * @return 订单商品列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单编号查询商品列表
     *
     * @param orderNo 订单编号
     * @return 订单商品列表
     */
    List<OrderItem> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据订单ID列表批量查询商品
     *
     * @param orderIds 订单ID列表
     * @return 订单商品列表
     */
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 插入订单商品
     *
     * @param record 订单商品
     * @return 影响行数
     */
    int insert(OrderItem record);

    /**
     * 批量插入订单商品
     *
     * @param list 订单商品列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<OrderItem> list);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据订单ID删除商品
     *
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
}