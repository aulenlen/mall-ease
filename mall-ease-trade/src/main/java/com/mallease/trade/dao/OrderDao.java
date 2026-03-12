package com.mallease.trade.dao;

import com.mallease.trade.model.client.query.OrderQuery;
import com.mallease.trade.model.client.vo.OrderStatusDistributionVO;
import com.mallease.trade.model.client.vo.OrderStatsTrendVO;
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
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 订单
     */
    Order selectByPrimaryKey(Long id);

    /**
     * 根据订单编号查询
     *
     * @param orderNo 订单编号
     * @return 订单
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 鎵归噺鏍规嵁璁㈠崟缂栧彿鏌ヨ
     *
     * @param orderNos 璁㈠崟缂栧彿鍒楄〃
     * @return 璁㈠崟鍒楄〃
     */
    List<Order> selectByOrderNos(@Param("orderNos") List<String> orderNos);

    /**
     * 根据幂等请求ID查询（防重复提交）
     *
     * @param requestId 幂等请求ID
     * @return 订单
     */
    Order selectByRequestId(@Param("requestId") String requestId);

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和状态查询订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 统计用户订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 插入订单
     *
     * @param record 订单
     * @return 影响行数
     */
    int insert(Order record);

    /**
     * 选择性插入订单（只插入非空字段）
     *
     * @param record 订单
     * @return 影响行数
     */
    int insertSelective(Order record);

    /**
     * 根据主键更新（全字段）
     *
     * @param record 订单
     * @return 影响行数
     */
    int updateByPrimaryKey(Order record);

    /**
     * 根据主键选择性更新（只更新非空字段）
     *
     * @param record 订单
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     * 更新订单状态
     *
     * @param id     主键ID
     * @param status 订单状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据订单编号更新状态
     *
     * @param orderNo 订单编号
     * @param status  订单状态
     * @return 影响行数
     */
    int updateStatusByOrderNo(@Param("orderNo") String orderNo, @Param("status") Integer status);

    /**
     * 根据主键删除
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据用户id和订单状态查询订单
     *
     * @param userId
     * @param status
     * @return 订单列表
     */
    List<Order> listByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    List<Order> listByStatusAndTime(@Param("status") int status, @Param("creatTime") LocalDateTime creatTime);

    /**
     * 查询需要处理的订单（超时未支付 或 库存待释放/释放失败）
     *
     * @param pendingStatus        待支付状态
     * @param expireTime           超时时间
     * @param cancelledStatus      订单取消状态
     * @param pendingReleaseStatus 待释放状态
     * @param releaseFailedStatus  释放失败状态
     * @return 订单列表
     */
    List<Order> listNeedProcess(@Param("pendingStatus") Integer pendingStatus,
                                @Param("expireTime") LocalDateTime expireTime,
                                @Param("cancelledStatus") Integer cancelledStatus,
                                @Param("pendingReleaseStatus") Integer pendingReleaseStatus,
                                @Param("releaseFailedStatus") Integer releaseFailedStatus);

    int updateBatchStatus(@Param("ids") List<Long> ids, @Param("status") int status);

    Order selectByConditions(@Param("userId") Long userId, @Param("orderNo") String orderNo, @Param("status") int status);

    /**
     * 批量更新订单状态和库存释放状态（根据订单编号）
     *
     * @param orderNos           订单编号列表
     * @param orderStatus        订单状态
     * @param stockReleaseStatus 库存释放状态
     * @return 影响行数
     */
    int batchUpdateStatusByOrderNos(@Param("orderNos") List<String> orderNos,
                                    @Param("orderStatus") Integer orderStatus,
                                    @Param("stockReleaseStatus") Integer stockReleaseStatus);


    /**
     * 管理端订单分页查询（多条件筛选）
     *
     * @param query 查询条件
     * @return 订单列表
     */
    List<Order> adminList(OrderQuery query);

    /**
     * 批量更新库存释放状态（根据订单编号）
     *
     * @param orderNos           订单编号列表
     * @param stockReleaseStatus 库存释放状态
     * @return 影响行数
     */
    int updateStockReleaseStatusByOrderNos(@Param("orderNos") List<String> orderNos,
                                           @Param("stockReleaseStatus") Integer stockReleaseStatus);

    /**
     * 今日订单统计（订单数量 + 订单金额）
     *
     * @param todayStart 今日开始时间
     * @return [count, sum_amount]
     */
    Map<String, Object> statsTodayOverview(@Param("todayStart") LocalDateTime todayStart);

    /**
     * 按状态统计订单数量
     *
     * @param status 订单状态
     * @return 数量
     */
    Long countByStatus(@Param("status") Integer status);

    /**
     * 全量订单统计
     *
     * @return [count, sum_amount]
     */
    Map<String, Object> statsTotalOverview();

    /**
     * 订单趋势统计（按日期聚合）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 趋势数据
     */
    List<OrderStatsTrendVO> statsTrend(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 订单状态分布统计
     *
     * @return 各状态订单数量
     */
    List<OrderStatusDistributionVO> statsStatusDistribution();

}
