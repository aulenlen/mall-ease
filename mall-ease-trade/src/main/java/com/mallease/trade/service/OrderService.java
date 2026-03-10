package com.mallease.trade.service;

import com.mallease.common.api.Page;
import com.mallease.common.enums.StockReleaseStatus;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.client.query.OrderQuery;
import com.mallease.trade.model.client.vo.OrderConfirmVO;
import com.mallease.trade.model.client.vo.OrderStatusDistributionVO;
import com.mallease.trade.model.client.vo.OrderStatsTrendVO;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.model.data.entity.OrderItem;
import jakarta.validation.constraints.NotBlank;

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
     * 查询用户订单列表（分页）
     */
    Page<OrderAggregate> listByUserId(Long userId, Integer status, int pageNum, int pageSize);

    /**
     * 取消订单（用户主动取消）
     */
    boolean cancel(String orderNo, Long userId);

    /**
     * 批量取消订单（定时任务/内部调用）
     *
     * @param orderNos 订单编号列表
     */
    void cancelByOrderNos(List<String> orderNos);

    /**
     * 获取快照
     */
    OrderConfirmVO getSnapshot(String requestId);

    /**
     * 删除快照
     */
    void deleteSnapshot(String requestId);

    /**
     * 查询需要处理的订单（超时未支付 或 库存释放失败）
     */
    List<Order> listNeedProcess();

    Map<String, StockReleaseStatus> tryReleaseStock(List<String> orderNos);

    int orderReleaseSuccess(List<String> released);

    int orderReleaseFailed(List<String> failed);

    Order findPending(Long userId, String orderNo);

    int updateStatus(String orderNo, int status);

    // ==================== 管理端方法 ====================

    /**
     * 根据订单编号查询订单实体
     *
     * @param orderNo 订单编号
     * @return 订单实体
     */
    Order findByOrderNo(String orderNo);

    /**
     * 管理端订单分页查询（多条件筛选）
     *
     * @param query 查询条件
     * @return 订单列表（需配合 PageHelper 使用）
     */
    List<Order> adminList(OrderQuery query);

    /**
     * 根据订单ID列表批量查询订单商品
     *
     * @param orderIds 订单ID列表
     * @return 订单商品列表
     */
    List<OrderItem> listItemsByOrderIds(List<Long> orderIds);

    /**
     * 根据订单编号查询订单商品
     *
     * @param orderNo 订单编号
     * @return 订单商品列表
     */
    List<OrderItem> listItemsByOrderNo(String orderNo);

    /**
     * 选择性更新订单（仅更新非 null 字段）
     *
     * @param order 订单实体（需设置 id 和待更新字段）
     * @return 影响行数
     */
    int updateOrderSelective(Order order);

    /**
     * 今日订单统计
     *
     * @param todayStart 今日开始时间
     * @return 统计结果（orderCount、orderAmount）
     */
    Map<String, Object> statsTodayOverview(LocalDateTime todayStart);

    /**
     * 按状态统计订单数量
     *
     * @param status 订单状态
     * @return 数量
     */
    Long countByStatus(Integer status);

    /**
     * 全量订单统计
     *
     * @return 统计结果（orderCount、orderAmount）
     */
    Map<String, Object> statsTotalOverview();

    /**
     * 订单趋势统计（按日期聚合）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 趋势数据
     */
    List<OrderStatsTrendVO> statsTrend(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 订单状态分布统计
     *
     * @return 各状态订单数量
     */
    List<OrderStatusDistributionVO> statsStatusDistribution();
}
