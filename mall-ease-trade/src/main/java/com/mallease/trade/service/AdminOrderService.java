package com.mallease.trade.service;

import com.mallease.common.api.Page;
import com.mallease.trade.model.client.cmd.ShipOrderCmd;
import com.mallease.trade.model.client.cmd.UpdateOrderCmd;
import com.mallease.trade.model.client.query.OrderQuery;
import com.mallease.trade.model.client.vo.AdminOrderVO;
import com.mallease.trade.model.client.vo.AdminShipmentVO;
import com.mallease.trade.model.data.entity.OrderOperationLog;

import java.util.List;

/**
 * 管理端订单服务接口
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
public interface AdminOrderService {

    /**
     * 管理端订单分页列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<AdminOrderVO> list(OrderQuery query);

    /**
     * 管理端订单详情
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    AdminOrderVO detail(String orderNo);

    /**
     * 发货
     *
     * @param cmd 发货命令
     */
    void ship(ShipOrderCmd cmd);

    /**
     * 查询物流信息
     *
     * @param orderNo 订单编号
     * @return 物流信息
     */
    AdminShipmentVO getShipment(String orderNo);

    /**
     * 强制取消订单
     *
     * @param orderNo 订单编号
     */
    void forceCancel(String orderNo);

    /**
     * 修改收货地址
     *
     * @param cmd 修改命令
     */
    void updateAddress(UpdateOrderCmd cmd);

    /**
     * 修改备注
     *
     * @param cmd 修改命令
     */
    void updateRemark(UpdateOrderCmd cmd);

    /**
     * 调整金额（仅限待支付订单）
     *
     * @param cmd 修改命令
     */
    void adjustAmount(UpdateOrderCmd cmd);

    /**
     * 查询操作日志
     *
     * @param orderNo 订单编号
     * @return 操作日志列表
     */
    List<OrderOperationLog> getOperationLogs(String orderNo);
}
