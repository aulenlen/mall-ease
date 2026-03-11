package com.mallease.product.service;

import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.product.model.data.entity.SkuStock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * SKU 库存服务接口。
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
public interface SkuStockService {

    /**
     * 创建单个库存记录。
     *
     * @param stock 库存实体
     * @return 库存ID
     */
    Long create(SkuStock stock);

    /**
     * 批量创建 SKU 库存。
     *
     * @param stockList 库存列表
     * @return 创建记录数
     */
    Integer createBatch(List<SkuStock> stockList);

    /**
     * 更新库存信息。
     *
     * @param stock 库存实体
     * @return 影响行数
     */
    int update(SkuStock stock);

    /**
     * 根据 SKU ID 获取库存。
     *
     * @param skuId SKU ID
     * @return 库存实体
     */
    SkuStock getBySkuId(Long skuId);

    /**
     * 手动调整库存，可用于入库或出库。
     *
     * @param skuId SKU ID
     * @param quantity 调整数，正数入库，负数出库
     * @return 影响行数
     */
    int adjustStock(Long skuId, Integer quantity);

    /**
     * 根据 SPU ID 列表获取库存。
     *
     * @param spuIdList SPU ID 列表
     * @return 库存列表
     */
    List<SkuStock> listStockBySpuIds(List<Long> spuIdList);

    /**
     * 根据 SKU ID 列表获取库存。
     *
     * @param skuIds SKU ID 列表
     * @return 库存列表
     */
    List<SkuStock> listStockBySkuIds(List<Long> skuIds);

    /**
     * 批量查询 SKU 是否有货。
     *
     * @param queries SPU 和 SKU 查询参数
     * @return SKU 可售状态列表
     */
    List<SkuAvailabilityDTO> listAvailabilityBySkuIds(List<SkuStockQueryDTO> queries);

    /**
     * 查询库存预警列表。
     *
     * @return 库存预警列表
     */
    List<SkuStock> listLowStockWarning();

    /**
     * 批量更新库存状态。
     *
     * @param skuIds SKU ID 列表
     * @param stockStatus 库存状态
     * @return 影响行数
     */
    int updateStockStatusBatch(List<Long> skuIds, Integer stockStatus);

    /**
     * 批量锁定库存，下单时使用。
     * 先通过 Redis 快速校验库存，拦截库存不足的请求。
     *
     * @param spuSkuQuantityMap SPU 维度库存映射，spuId -> skuId -> quantity
     * @param orderNo 订单编号
     * @param expireTime 订单过期时间
     */
    void lockStock(Map<Long, Map<Long, Integer>> spuSkuQuantityMap, String orderNo, LocalDateTime expireTime);

    /**
     * 释放订单库存。
     *
     * @param orderNos 订单号列表
     * @return 释放失败的订单号列表
     */
    List<String> unlockStock(List<String> orderNos);

    /**
     * 释放过期的库存预占，定时任务调用。
     * 每条预占记录的库存释放和状态更新都在同一事务内完成。
     *
     * @param limit 每次处理的最大数量
     * @return 成功释放的记录数
     */
    int releaseExpiredReservations(int limit);
}
