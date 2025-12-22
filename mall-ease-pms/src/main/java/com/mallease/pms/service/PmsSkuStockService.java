package com.mallease.pms.service;

import com.mallease.pms.pojo.PmsSkuStock;
import com.mallease.pms.pojo.PmsSpu;

import java.util.List;

/**
 * SKU库存服务接口
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
public interface PmsSkuStockService {
    /**
     * 批量创建 sku库存
     * @return 创建的记录数
     */
    Integer createBatch(List<PmsSkuStock> stockList);


    /**
     * 根据产品ID和关键字模糊查询SKU库存
     *
     * @param productId 产品ID
     * @param keyword 关键字 (可选，用于模糊匹配 sku_code)
     * @return SKU库存列表
     */
    List<PmsSkuStock> getByProductIdAndKeyword(Long productId, String keyword);

    /**
     * 批量更新SKU库存信息
     *
     * @param productId 产品ID（用于验证）
     * @param skuStockList SKU库存列表
     * @return 更新的记录数
     */
    int updateBatch(Long productId, List<PmsSkuStock> skuStockList);

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
    List<PmsSkuStock> listStockBySpuIds(List<Long> spuIdList);
}
