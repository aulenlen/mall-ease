package com.mallease.trade.service.order;

import com.mallease.common.api.Page;
import com.mallease.trade.controller.portal.order.vo.OrderConfirmRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitReqVO;
import com.mallease.trade.service.order.model.OrderAggregate;
import com.mallease.trade.controller.admin.order.vo.OrderPageReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsTrendRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatusDistributionRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitRespVO;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
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
     * 提交订单
     */
    OrderSubmitRespVO submitOrder(Long userId, OrderSubmitReqVO reqVO);

    /**
     * 根据订单编号查询
     */
    OrderAggregate getOrderAggregate(String orderNo);

    /**
     * 查询用户订单列表（分页）
     */
    Page<OrderAggregate> pageUserOrders(Long userId, Integer status, int pageNum, int pageSize);

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
     * 获取快照
     */
    OrderConfirmRespVO getOrderSnapshot(String requestId);

    /**
     * 删除快照
     */
    void deleteOrderSnapshot(String requestId);

    Order findPendingPaymentOrder(Long userId, String orderNo);

    int updateOrderStatus(String orderNo, int status);

    void confirmPaidOrder(String orderNo);

    void recoverLockingOrders(int limit);

    void closeExpiredPendingOrders(int limit);

    void retryPayConfirmingOrders(int limit);

    void retryReleasingOrders(int limit);

    // ==================== 管理端方法 ====================

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
     * @param query 查询条件
     * @return 订单列表（需配合 PageHelper 使用）
     */
    List<Order> listAdminOrders(OrderPageReqVO query);

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
