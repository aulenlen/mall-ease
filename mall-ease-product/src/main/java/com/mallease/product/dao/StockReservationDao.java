package com.mallease.product.dao;

import com.mallease.product.model.data.entity.StockReservation;
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
     * 根据订单号和状态查询预占记录
     *
     * @param orderNo 订单编号
     * @param status  预占状态
     * @return 预占记录列表
     */
    List<StockReservation> selectByOrderNoAndStatus(@Param("orderNo") String orderNo,
                                                    @Param("status") Integer status);

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
     * @param status 预占状态（LOCKED=1）
     * @param limit  每次处理的最大数量
     * @return 已过期的预占记录列表
     */
    List<StockReservation> selectExpiredByStatus(@Param("status") Integer status,
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
     * @param id     主键ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 按订单号批量更新状态（释放/确认时使用）
     *
     * @param orderNo   订单编号
     * @param oldStatus 原状态（防止重复操作）
     * @param newStatus 新状态
     * @return 影响行数
     */
    int updateStatusByOrderNo(@Param("orderNo") String orderNo,
                              @Param("oldStatus") Integer oldStatus,
                              @Param("newStatus") Integer newStatus);

    /**
     * 根据主键删除（慎用，建议保留记录用于审计）
     *
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 按预约记录ID批量更新状态
     *
     * @param ids    预约记录ID列表
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 按订单号和状态查询预约记录
     *
     * @param orderNos 订单编号列表
     * @param status   预约状态
     * @return 预约记录列表
     */
    List<StockReservation> listByOrderNosAndStatus(@Param("orderNos") List<String> orderNos, @Param("status") Integer status);
}
