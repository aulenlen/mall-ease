package com.mallease.trade.service.order;

import com.mallease.common.api.Page;
import com.mallease.trade.controller.admin.order.vo.OrderShipReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderUpdateReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderPageReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderAdminRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderShipmentRespVO;
import com.mallease.trade.dal.entity.OrderOperationLog;

import java.util.List;

/**
 * 管理端订单服务接口
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
public interface OrderAdminService {

    /**
     * 管理端订单分页列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<OrderAdminRespVO> list(OrderPageReqVO query);

    /**
     * 管理端订单详情
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    OrderAdminRespVO detail(String orderNo);

    /**
     * 发货
     *
     * @param cmd 发货命令
     */
    void ship(OrderShipReqVO cmd);

    /**
     * 查询物流信息
     *
     * @param orderNo 订单编号
     * @return 物流信息
     */
    OrderShipmentRespVO getShipment(String orderNo);

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
    void updateAddress(OrderUpdateReqVO cmd);

    /**
     * 修改备注
     *
     * @param cmd 修改命令
     */
    void updateRemark(OrderUpdateReqVO cmd);

    /**
     * 调整金额（仅限待支付订单）
     *
     * @param cmd 修改命令
     */
    void adjustAmount(OrderUpdateReqVO cmd);

    /**
     * 查询操作日志
     *
     * @param orderNo 订单编号
     * @return 操作日志列表
     */
    List<OrderOperationLog> getOperationLogs(String orderNo);
}
