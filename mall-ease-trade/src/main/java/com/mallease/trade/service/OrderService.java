package com.mallease.trade.service;

import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.client.vo.OrderConfirmVO;
import com.mallease.trade.model.data.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
public interface OrderService {

    /**
     * 生成结算快照
     */
    OrderConfirmVO generateSnapshot(Long userId, List<Long> cartItemIds);

    /**
     * 提交订单
     */
    String submit(Long userId, Order order, String requestId);

    /**
     * 创建订单（内部使用）
     */
    String create(OrderAggregate aggregate);

    /**
     * 根据订单编号查询
     */
    OrderAggregate getByOrderNo(String orderNo);

    /**
     * 查询用户订单列表
     */
    List<OrderAggregate> listByUserId(Long userId, Integer status);

    /**
     * 取消订单
     */
    boolean cancel(String orderNo, Long userId);

    /**
     * 获取快照
     */
    OrderConfirmVO getSnapshot(String requestId);

    /**
     * 删除快照
     */
    void deleteSnapshot(String requestId);
}
