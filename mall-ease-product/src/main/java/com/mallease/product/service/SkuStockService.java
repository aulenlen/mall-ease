package com.mallease.product.service;

import com.mallease.product.model.data.entity.SkuStock;

import java.util.List;

/**
 * SKU库存服务接口
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
public interface SkuStockService {
    /**
     * 批量创建 sku库存
     * @return 创建的记录数
     */
    Integer createBatch(List<SkuStock> stockList);

    /**
     * 扣减库存（原子操作）
     *
     * @param productId 商品ID（用于定位Hash key）
     * @param skuId SKU ID（用于定位Hash field）
     * @param quantity 扣减数量
     * @return true=扣减成功, false=库存不足
     */
    boolean deductStock(Long productId, Long skuId, Integer quantity);

    /**
     * 获取库存
     * @param spuIdList spuId列表
     * @return 库存列表
     */
    List<SkuStock> listStockBySpuIds(List<Long> spuIdList);

    /**
     * 根据SKU ID列表获取库存
     * @param skuIds SKU ID列表
     * @return 库存列表
     */
    List<SkuStock> listStockBySkuIds(List<Long> skuIds);
}
