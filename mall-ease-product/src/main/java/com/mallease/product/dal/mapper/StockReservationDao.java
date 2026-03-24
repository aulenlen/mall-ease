package com.mallease.product.dal.mapper;

import com.mallease.product.dal.entity.StockReservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存预占明细 Mapper 接口
 *
 * @author: Aulen
 * @create: 2026-02-01
 */
@Mapper
public interface StockReservationDao {

    /**
     * 根据主键查询
     *
     * @param id 主键ID
     * @return 预占记录
     */
    StockReservation selectByPrimaryKey(Long id);

    /**
     * 根据订单号查询预占记录列表
     *
     * @param orderNo 订单编号
     * @return 预占记录列表
     */
    List<StockReservation> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据订单号和预占状态查询预占记录
     *
     * @param orderNo           订单编号
     * @param reservationStatus 预占状态
     * @return 预占记录列表
     */
    List<StockReservation> selectByOrderNoAndReservationStatus(@Param("orderNo") String orderNo,
                                                               @Param("reservationStatus") Integer reservationStatus);

    /**
     * 根据订单号和SKU ID查询（幂等检查）
     *
     * @param orderNo 订单编号
     * @param skuId   SKU ID
     * @return 预占记录
     */
    StockReservation selectByOrderNoAndSkuId(@Param("orderNo") String orderNo,
                                             @Param("skuId") Long skuId);

    /**
     * 查询已过期的锁定记录（用于定时任务释放库存）
     *
     * @param reservationStatus 预占状态（LOCKED=1）
     * @param limit             每次处理的最大数量
     * @return 已过期的预占记录列表
     */
    List<StockReservation> selectExpiredByReservationStatus(@Param("reservationStatus") Integer reservationStatus,
                                                            @Param("limit") Integer limit);

    /**
     * 检查订单是否存在预占记录（任意状态）
     *
     * @param orderNo 订单编号
     * @return 记录数量
     */
    int countByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 插入预占记录
     *
     * @param record 预占记录
     * @return 影响行数
     */
    int insert(StockReservation record);

    /**
     * 批量插入预占记录
     *
     * @param list 预占记录列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<StockReservation> list);

    /**
     * 更新预占状态（通用）
     *
     * @param id                主键ID
     * @param reservationStatus 新状态
     * @return 影响行数
     */
    int updateReservationStatus(@Param("id") Long id, @Param("reservationStatus") Integer reservationStatus);

    /**
     * 按订单号批量更新预占状态（释放/确认时使用）
     *
     * @param orderNo              订单编号
     * @param oldReservationStatus 原状态（防止重复操作）
     * @param newReservationStatus 新状态
     * @return 影响行数
     */
    int updateReservationStatusByOrderNo(@Param("orderNo") String orderNo,
                                         @Param("oldReservationStatus") Integer oldReservationStatus,
                                         @Param("newReservationStatus") Integer newReservationStatus);

    /**
     * 根据主键删除（慎用，建议保留记录用于审计）
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 按预约记录ID批量更新预占状态
     *
     * @param ids               预约记录ID列表
     * @param reservationStatus 新状态
     * @return 影响行数
     */
    int updateReservationStatusByIds(@Param("ids") List<Long> ids, @Param("reservationStatus") Integer reservationStatus);

    /**
     * 按订单号和预占状态查询预约记录
     *
     * @param orderNos          订单编号列表
     * @param reservationStatus 预约状态
     * @return 预约记录列表
     */
    List<StockReservation> listByOrderNosAndReservationStatus(@Param("orderNos") List<String> orderNos,
                                                              @Param("reservationStatus") Integer reservationStatus);
}
