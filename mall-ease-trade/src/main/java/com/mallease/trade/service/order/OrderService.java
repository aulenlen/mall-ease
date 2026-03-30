package com.mallease.trade.service.order;

import com.mallease.common.api.Page;
import com.mallease.trade.controller.admin.order.vo.OrderAdminRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderConfirmRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderPageReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderShipReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderShipmentRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsOverviewRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsTrendRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatusDistributionRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderUpdateReqVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitRespVO;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.dal.entity.OrderOperationLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单领域服务。
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
public interface OrderService {

    /**
     * 根据用户已勾选购物车项生成结算快照。
     *
     * @param userId 用户ID
     */
    OrderConfirmRespVO createOrderSnapshot(Long userId);

    /**
     * 根据确认页返回的 requestId 提交订单。
     */
    OrderSubmitRespVO submitOrder(Long userId, OrderSubmitReqVO reqVO);

    /**
     * 查询指定用户的订单详情。
     */
    OrderRespVO getUserOrderDetail(Long userId, String orderNo);

    /**
     * 查询用户订单列表（分页）
     */
    Page<OrderRespVO> pageUserOrders(Long userId, Integer status, int pageNum, int pageSize);

    /**
     * 管理端订单分页列表。
     */
    Page<OrderAdminRespVO> pageAdminOrders(OrderPageReqVO reqVO);

    /**
     * 管理端订单详情。
     */
    OrderAdminRespVO getAdminOrderDetail(String orderNo);

    /**
     * 管理端发货。
     */
    void shipOrder(OrderShipReqVO reqVO);

    /**
     * 查询物流信息。
     */
    OrderShipmentRespVO getOrderShipment(String orderNo);

    /**
     * 管理端强制取消订单。
     */
    void forceCancelOrder(String orderNo);

    /**
     * 管理端修改收货地址。
     */
    void updateOrderAddress(OrderUpdateReqVO reqVO);

    /**
     * 管理端修改备注。
     */
    void updateOrderRemark(OrderUpdateReqVO reqVO);

    /**
     * 管理端调整订单金额。
     */
    void adjustOrderAmount(OrderUpdateReqVO reqVO);

    /**
     * 查询订单操作日志。
     */
    List<OrderOperationLog> listOrderOperationLogs(String orderNo);

    /**
     * 管理端订单统计总览。
     */
    OrderStatsOverviewRespVO getOrderStatsOverview();

    /**
     * 管理端订单趋势统计。
     */
    List<OrderStatsTrendRespVO> listAdminOrderStatsTrend(Integer days);

    /**
     * 管理端订单状态分布。
     */
    List<OrderStatusDistributionRespVO> listAdminOrderStatusDistribution();

    /**
     * 取消订单（用户主动取消）
     */
    boolean cancelOrder(String orderNo, Long userId, boolean restoreCart);

    /**
     * 批量取消订单（定时任务/内部调用）
     *
     * @param orderNos 订单编号列表
     */
    void cancelOrders(List<String> orderNos);

    /**
     * 查询指定用户下待支付订单，用于支付链路校验订单归属和状态。
     */
    Order findPendingPaymentOrder(Long userId, String orderNo);

    /**
     * 仅更新订单状态字段，适合支付回调等轻量状态推进场景。
     */
    int updateOrderStatus(String orderNo, int status);

    /**
     * 支付成功后将订单状态推进到已支付+确认中
     */
    void advanceOrderToPaid(String orderNo);

    /**
     * 支付成功后确认库存扣减，并把订单推进到待发货状态。
     */
    void confirmPaidOrder(String orderNo);

    /**
     * 补偿处理锁库中或锁库结果未知的订单，避免异常链路长期停留在处理中。
     */
    void recoverLockingOrders(int limit);

    /**
     * 关闭已过支付时效的待支付订单，并释放已锁定库存。
     */
    void closeExpiredPendingOrders(int limit);

    /**
     * 重试支付成功但库存确认未完成的订单。
     */
    void retryPayConfirmingOrders(int limit);

    /**
     * 重试库存释放失败的已取消订单。
     */
    void retryReleasingOrders(int limit);

    /**
     * 根据订单编号查询订单实体
     *
     * @param orderNo 订单编号
     * @return 订单实体
     */
    Order getOrder(String orderNo);

    /**
     * 管理端订单分页查询（多条件筛选）
     *
     * @param reqVO 查询条件
     * @return 订单列表（需配合 PageHelper 使用）
     */
    List<Order> listAdminOrders(OrderPageReqVO reqVO);

    /**
     * 根据订单ID列表批量查询订单商品
     *
     * @param orderIds 订单ID列表
     * @return 订单商品列表
     */
    List<OrderItem> listOrderItemsByOrderIds(List<Long> orderIds);

    /**
     * 根据订单编号查询订单商品
     *
     * @param orderNo 订单编号
     * @return 订单商品列表
     */
    List<OrderItem> listOrderItemsByOrderNo(String orderNo);

    /**
     * 选择性更新订单（仅更新非 null 字段）
     *
     * @param order 订单实体（需设置 id 和待更新字段）
     * @return 影响行数
     */
    int updateOrder(Order order);

    /**
     * 今日订单统计
     *
     * @param todayStart 今日开始时间
     * @return 统计结果（orderCount、orderAmount）
     */
    Map<String, Object> getTodayOrderOverview(LocalDateTime todayStart);

    /**
     * 按状态统计订单数量
     *
     * @param status 订单状态
     * @return 数量
     */
    Long countOrdersByStatus(Integer status);

    /**
     * 全量订单统计
     *
     * @return 统计结果（orderCount、orderAmount）
     */
    Map<String, Object> getTotalOrderOverview();

    /**
     * 订单趋势统计（按日期聚合）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 趋势数据
     */
    List<OrderStatsTrendRespVO> listOrderStatsTrend(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 订单状态分布统计
     *
     * @return 各状态订单数量
     */
    List<OrderStatusDistributionRespVO> listOrderStatusDistribution();
}
