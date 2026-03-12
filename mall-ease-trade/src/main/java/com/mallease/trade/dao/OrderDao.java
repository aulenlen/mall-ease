package com.mallease.trade.dao;

import com.mallease.trade.model.client.query.OrderQuery;
import com.mallease.trade.model.client.vo.OrderStatsTrendVO;
import com.mallease.trade.model.client.vo.OrderStatusDistributionVO;
import com.mallease.trade.model.data.entity.Order;
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
     * 根据主键删除订单
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据用户ID和订单状态查询订单
     */
    List<Order> listByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 根据状态和时间查询订单
     */
    List<Order> listByStatusAndTime(@Param("status") int status, @Param("creatTime") LocalDateTime creatTime);

    /**
     * 查询需要处理的订单
     *
     * 场景包括：
     * 1. 超时未支付
     * 2. 已取消但库存待释放
     * 3. 已取消且库存释放失败，等待重试
     */
    List<Order> listNeedProcess(@Param("pendingStatus") Integer pendingStatus,
                                @Param("expireTime") LocalDateTime expireTime,
                                @Param("cancelledStatus") Integer cancelledStatus,
                                @Param("pendingReleaseStatus") Integer pendingReleaseStatus,
                                @Param("releaseFailedStatus") Integer releaseFailedStatus);

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
                                    @Param("stockReleaseStatus") Integer stockReleaseStatus);

    /**
     * 管理端分页查询订单
     */
    List<Order> adminList(OrderQuery query);

    /**
     * 批量更新库存释放状态
     */
    int updateStockReleaseStatusByOrderNos(@Param("orderNos") List<String> orderNos,
                                           @Param("stockReleaseStatus") Integer stockReleaseStatus);

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
    List<OrderStatsTrendVO> statsTrend(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 订单状态分布统计
     */
    List<OrderStatusDistributionVO> statsStatusDistribution();
}
