package com.mallease.trade.service;

import com.mallease.trade.model.aggregate.OrderAggregate;

import java.util.List;

/**
 * 订单服务接口
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param aggregate 订单聚合对象（包含 Order + OrderItem 列表）
     * @return 订单编号
     */
    String create(OrderAggregate aggregate);

    /**
     * 根据订单编号查询订单详情
     *
     * @param orderNo 订单编号
     * @return 订单聚合对象（含商品列表）
     */
    OrderAggregate getByOrderNo(String orderNo);

    /**
     * 查询用户订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态（可选，null 表示全部）
     * @return 订单聚合对象列表
     */
    List<OrderAggregate> listByUserId(Long userId, Integer status);

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @param userId  用户ID（校验归属）
     * @return 是否成功
     */
    boolean cancel(String orderNo, Long userId);
}
