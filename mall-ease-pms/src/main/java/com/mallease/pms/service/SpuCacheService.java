package com.mallease.pms.service;

import com.mallease.pms.dto.cache.PmsSpuCacheDTO;

import java.util.List;
import java.util.Map;

/**
 * SPU缓存服务接口
 * <p>
 * 提供SPU商品详情的缓存预热、查询、失效等功能
 * 独立于业务逻辑层，遵循单一职责原则
 *
 * 缓存策略：
 * 1. SPU详情：String结构，Key = spu:detail:{spuId}
 * 2. SKU库存：Hash结构，Key = spu:sku:stock:{spuId}，Field = {skuId}
 *
 * @author: Aulen
 * @create: 2025-12-22
 */
public interface SpuCacheService {

    // ==================== 缓存预热 ====================

    /**
     * 预热单个SPU缓存
     * <p>
     * 包括SPU详情和SKU库存两部分
     *
     * @param spuId SPU ID
     */
    void warmUp(Long spuId);

    /**
     * 批量预热SPU缓存
     * <p>
     * 适用于商品批量上架场景
     *
     * @param spuIds SPU ID列表
     */
    void warmUpBatch(List<Long> spuIds);

    // ==================== 缓存查询 ====================

    /**
     * 获取SPU缓存详情
     * <p>
     * 优先从缓存获取，缓存未命中时返回null（不穿透数据库）
     *
     * @param spuId SPU ID
     * @return SPU缓存对象，缓存未命中返回null
     */
    PmsSpuCacheDTO get(Long spuId);

    /**
     * 批量获取SPU缓存详情
     *
     * @param spuIds SPU ID列表
     * @return SPU ID到缓存对象的映射（只包含命中的记录）
     */
    Map<Long, PmsSpuCacheDTO> getBatch(List<Long> spuIds);

    /**
     * 获取SKU库存
     *
     * @param spuId SPU ID
     * @param skuId SKU ID
     * @return 库存数量，缓存未命中返回null
     */
    Integer getSkuStock(Long spuId, Long skuId);

    /**
     * 批量获取单个SPU下所有SKU库存
     *
     * @param spuId SPU ID
     * @return SKU ID到库存的映射
     */
    Map<Long, Integer> getSkuStockBySpu(Long spuId);

    // ==================== 缓存失效 ====================

    /**
     * 删除单个SPU缓存
     * <p>
     * 包括SPU详情和SKU库存
     *
     * @param spuId SPU ID
     */
    void evict(Long spuId);

    /**
     * 批量删除SPU缓存
     * <p>
     * 适用于商品批量下架场景
     *
     * @param spuIds SPU ID列表
     */
    void evictBatch(List<Long> spuIds);

    // ==================== 库存操作 ====================

    /**
     * 扣减SKU库存（原子操作）
     * <p>
     * 使用 HINCRBY 实现原子扣减
     *
     * @param spuId    SPU ID
     * @param skuId    SKU ID
     * @param quantity 扣减数量（正数）
     * @return 扣减后的库存，若扣减后小于0则返回null表示库存不足
     */
    Long decreaseStock(Long spuId, Long skuId, int quantity);

    /**
     * 恢复SKU库存（原子操作）
     * <p>
     * 用于订单取消等场景
     *
     * @param spuId    SPU ID
     * @param skuId    SKU ID
     * @param quantity 恢复数量（正数）
     * @return 恢复后的库存
     */
    Long increaseStock(Long spuId, Long skuId, int quantity);

    /**
     * 批量扣减库存
     * <p>
     * 用于下单时一次性扣减多个SKU库存
     *
     * @param stockDecrements 库存扣减映射：spuId -> (skuId -> quantity)
     * @return 是否全部扣减成功
     */
    boolean decreaseStockBatch(Map<Long, Map<Long, Integer>> stockDecrements);
}
