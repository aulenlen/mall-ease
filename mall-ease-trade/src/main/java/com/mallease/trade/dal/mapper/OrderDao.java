package com.mallease.trade.dal.mapper;

import com.mallease.trade.controller.admin.order.vo.OrderPageReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsTrendRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatusDistributionRespVO;
import com.mallease.trade.dal.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
@Mapper
public interface OrderDao {

    /**
     * 根据主键查询订单
     */
    Order selectByPrimaryKey(Long id);

    /**
     * 根据订单编号查询订单
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据订单编号列表批量查询订单
     */
    List<Order> selectByOrderNos(@Param("orderNos") List<String> orderNos);

    /**
     * 根据幂等请求ID查询订单
     */
    Order selectByRequestId(@Param("requestId") String requestId);

    /**
     * 根据用户ID查询订单列表
     */
    List<Order> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和状态查询订单列表
     */
    List<Order> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计用户订单数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 插入订单
     */
    int insert(Order record);

    /**
     * 选择性插入订单
     */
    int insertSelective(Order record);

    /**
     * 根据主键全量更新订单
     */
    int updateByPrimaryKey(Order record);

    /**
     * 根据主键选择性更新订单
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * 根据主键更新订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据订单编号更新订单状态
     */
    int updateStatusByOrderNo(@Param("orderNo") String orderNo, @Param("status") Integer status);

    /**
     * 基于当前状态更新订单状态和库存处理状态。
     */
    int updateStatusAndStockProcessByOrderNo(@Param("orderNo") String orderNo,
                                             @Param("oldStatus") Integer oldStatus,
                                             @Param("newStatus") Integer newStatus,
                                             @Param("stockProcessStatus") Integer stockProcessStatus);

    /**
     * 按订单编号批量更新订单状态和库存处理状态，并校验原状态。
     */
    int batchUpdateStatusAndStockProcessByOrderNos(@Param("orderNos") List<String> orderNos,
                                                   @Param("oldStatus") Integer oldStatus,
                                                   @Param("newStatus") Integer newStatus,
                                                   @Param("stockProcessStatus") Integer stockProcessStatus);

    /**
     * 根据主键删除订单
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据用户ID和订单状态查询订单
     */
    List<Order> listByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 查询指定状态的订单。
     */
    List<Order> listByStatuses(@Param("statuses") List<Integer> statuses, @Param("limit") Integer limit);

    /**
     * 按订单状态和库存处理状态联合查询订单。
     */
    List<Order> listByStatusAndStockProcessStatuses(@Param("orderStatus") Integer orderStatus,
                                                    @Param("stockProcessStatuses") List<Integer> stockProcessStatuses,
                                                    @Param("limit") Integer limit);

    /**
     * 查询已超时的待支付订单。
     */
    List<Order> listExpiredPendingPayment(@Param("status") Integer status,
                                          @Param("expireTime") LocalDateTime expireTime,
                                          @Param("limit") Integer limit);

    /**
     * 批量更新订单状态
     */
    int updateBatchStatus(@Param("ids") List<Long> ids, @Param("status") int status);

    /**
     * 根据用户ID、订单编号和状态查询订单
     */
    Order selectByConditions(@Param("userId") Long userId, @Param("orderNo") String orderNo, @Param("status") int status);

    /**
     * 批量更新订单状态和库存释放状态
     */
    int batchUpdateStatusByOrderNos(@Param("orderNos") List<String> orderNos,
                                    @Param("orderStatus") Integer orderStatus,
                                    @Param("stockProcessStatus") Integer stockProcessStatus);

    /**
     * 管理端分页查询订单
     */
    List<Order> adminList(OrderPageReqVO query);

    /**
     * 批量更新库存释放状态
     */
    int updateStockProcessStatusByOrderNos(@Param("orderNos") List<String> orderNos,
                                           @Param("stockProcessStatus") Integer stockProcessStatus);

    /**
     * 今日订单概览统计
     */
    Map<String, Object> statsTodayOverview(@Param("todayStart") LocalDateTime todayStart);

    /**
     * 按状态统计订单数量
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 全量订单概览统计
     */
    Map<String, Object> statsTotalOverview();

    /**
     * 订单趋势统计
     */
    List<OrderStatsTrendRespVO> statsTrend(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 订单状态分布统计
     */
    List<OrderStatusDistributionRespVO> statsStatusDistribution();
}
