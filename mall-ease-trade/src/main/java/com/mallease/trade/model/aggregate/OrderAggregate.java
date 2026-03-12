package com.mallease.trade.model.aggregate;

import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 订单聚合对象
 *
 * 封装订单主表 + 订单商品列表，作为订单领域的聚合根
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAggregate {

    /**
     * 订单主表
     */
    private Order order;

    /**
     * 订单商品列表
     */
    private List<OrderItem> items;

    /**
     * 订单商品总件数（sum(quantity)）
     */
    private Integer totalQuantity;

    /**
     * 判断是否为创建操作
     */
    public boolean isCreate() {
        return order == null || order.getId() == null;
    }

    /**
     * 获取订单ID
     */
    public Long getOrderId() {
        return order != null ? order.getId() : null;
    }

    /**
     * 获取订单编号
     */
    public String getOrderNo() {
        return order != null ? order.getOrderNo() : null;
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return order != null ? order.getUserId() : null;
    }
}
