package com.mallease.product.service;

import com.mallease.product.model.data.entity.SkuStock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * SKU库存服务接口
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
public interface SkuStockService {

    /**
     * 创建单个库存记录
     *
     * @param stock 库存实体
     * @return 库存ID
     */
    Long create(SkuStock stock);

    /**
     * 批量创建 sku库存
     *
     * @param stockList 库存列表
     * @return 创建的记录数
     */
    Integer createBatch(List<SkuStock> stockList);

    /**
     * 更新库存信息
     *
     * @param stock 库存实体
     * @return 影响行数
     */
    int update(SkuStock stock);

    /**
     * 根据SKU ID获取库存
     *
     * @param skuId SKU ID
     * @return 库存实体
     */
    SkuStock getBySkuId(Long skuId);

    /**
     * 手动调整库存（入库/出库）
     *
     * @param skuId SKU ID
     * @param quantity 调整数量（正数入库，负数出库）
     * @return 影响行数
     */
    int adjustStock(Long skuId, Integer quantity);

    /**
     * 获取库存
     *
     * @param spuIdList spuId列表
     * @return 库存列表
     */
    List<SkuStock> listStockBySpuIds(List<Long> spuIdList);

    /**
     * 根据SKU ID列表获取库存
     *
     * @param skuIds SKU ID列表
     * @return 库存列表
     */
    List<SkuStock> listStockBySkuIds(List<Long> skuIds);

    /**
     * 查询库存预警列表
     *
     * @return 库存预警列表
     */
    List<SkuStock> listLowStockWarning();

    /**
     * 批量更新库存状态
     *
     * @param skuIds SKU ID列表
     * @param stockStatus 库存状态
     * @return 影响行数
     */
    int updateStockStatusBatch(List<Long> skuIds, Integer stockStatus);

    /**
     * 批量锁定库存（下单时使用）
     * 先通过 Redis 快速校验库存，拦截库存不足的请求
     *
     * @param spuSkuQuantityMap SPU 维度库存映射（spuId → skuId → quantity）
     * @param orderNo           订单编号
     * @param expireTime        订单过期时间
     */
    void lockStock(Map<Long, Map<Long, Integer>> spuSkuQuantityMap, String orderNo, LocalDateTime expireTime);

    List<String> unlockStock(List<String> orderNos);


    /**
     * 释放过期的库存预占（定时任务调用）
     * 逐条处理确保原子性：每条预占记录的库存释放和状态更新在同一事务中完成
     *
     * @param limit 每次处理的最大数量
     * @return 成功释放的记录数
     */
    int releaseExpiredReservations(int limit);
}
