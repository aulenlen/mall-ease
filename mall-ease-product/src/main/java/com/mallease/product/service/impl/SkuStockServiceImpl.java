package com.mallease.product.service.impl;

import com.mallease.product.constant.RedisKey;
import com.mallease.product.component.CacheService;
import com.mallease.product.dao.SkuStockDao;
import com.mallease.product.model.data.entity.SkuStock;
import com.mallease.product.service.SkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SKU库存服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
@Slf4j
@Service
public class SkuStockServiceImpl implements SkuStockService {
    @Autowired
    private SkuStockDao skuStockDao;
    @Autowired
    private CacheService cacheService;

    @Override
    public Integer createBatch(List<SkuStock> stockList) {
        return skuStockDao.insertBatch(stockList);
    }

    @Override
    public boolean deductStock(Long spuId, Long skuId, Integer quantity) {
        String hashKey = String.valueOf(skuId);
        Long newStock = cacheService.deductStockAtomic(
                RedisKey.SPU_SKU_STOCK, spuId, hashKey, quantity
        );
        if (newStock != null) {
            log.info("扣减SKU库存成功，spuId={}, skuId={}, quantity={}, 剩余={}",
                    spuId, skuId, quantity, newStock);

            // 检查是否售罄
            if (newStock == 0) {
                log.warn("SKU已售罄，spuId={}, skuId={}", spuId, skuId);
                // TODO: 通知ES更新商品库存状态（如果所有SKU都售罄）
            }

            return true;
        }

        log.warn("SKU库存不足，spuId={}, skuId={}, 尝试扣减={}", spuId, skuId, quantity);
        return false;
    }

    @Override
    public List<SkuStock> listStockBySpuIds(List<Long> spuList) {
        return skuStockDao.selectBySpuIds(spuList);
    }

    @Override
    public List<SkuStock> listStockBySkuIds(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return List.of();
        }
        return skuStockDao.selectBySkuIds(skuIds);
    }
//    @Scheduled(cron = "0 */5 * * * ?")  // 每5分钟
//    public void syncStockToDatabase() {
//        // 从Redis读取库存变化，批量更新数据库
//        // 避免每次扣减都写数据库，减轻DB压力
//    }

}

