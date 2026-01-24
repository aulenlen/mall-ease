package com.mallease.product.service.impl;

import com.mallease.common.exception.ApiException;
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
    public Long create(SkuStock stock) {
        if (stock == null || stock.getSkuId() == null) {
            throw new ApiException("SKU ID不能为空");
        }

        // 检查是否已存在
        SkuStock existing = skuStockDao.selectBySkuId(stock.getSkuId());
        if (existing != null) {
            throw new ApiException("该SKU已存在库存记录");
        }

        stock.setLockStock(stock.getLockStock() != null ? stock.getLockStock() : 0);
        stock.setSale(stock.getSale() != null ? stock.getSale() : 0);
        skuStockDao.insertSelective(stock);
        return stock.getId();
    }

    @Override
    public Integer createBatch(List<SkuStock> stockList) {
        return skuStockDao.insertBatch(stockList);
    }

    @Override
    public int update(SkuStock stock) {
        if (stock == null || stock.getId() == null) {
            throw new ApiException("库存ID不能为空");
        }

        SkuStock existing = skuStockDao.selectByPrimaryKey(stock.getId());
        if (existing == null) {
            throw new ApiException("库存记录不存在");
        }

        return skuStockDao.updateByPrimaryKeySelective(stock);
    }

    @Override
    public SkuStock getBySkuId(Long skuId) {
        if (skuId == null) {
            return null;
        }
        return skuStockDao.selectBySkuId(skuId);
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
            }

            return true;
        }

        log.warn("SKU库存不足，spuId={}, skuId={}, 尝试扣减={}", spuId, skuId, quantity);
        return false;
    }

    @Override
    public int adjustStock(Long skuId, Integer quantity) {
        if (skuId == null) {
            throw new ApiException("SKU ID不能为空");
        }
        if (quantity == null || quantity == 0) {
            throw new ApiException("调整数量不能为空或0");
        }

        SkuStock existing = skuStockDao.selectBySkuId(skuId);
        if (existing == null) {
            throw new ApiException("库存记录不存在");
        }

        int result;
        if (quantity > 0) {
            // 入库
            result = skuStockDao.increaseStock(skuId, quantity);
            log.info("库存入库成功，skuId={}, 入库数量={}", skuId, quantity);
        } else {
            // 出库（需要检查库存是否足够）
            int absQuantity = Math.abs(quantity);
            if (existing.getStock() < absQuantity) {
                throw new ApiException("库存不足，当前库存: " + existing.getStock());
            }
            result = skuStockDao.increaseStock(skuId, quantity); // 负数实现减少
            log.info("库存出库成功，skuId={}, 出库数量={}", skuId, absQuantity);
        }

        return result;
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

    @Override
    public List<SkuStock> listLowStockWarning() {
        return skuStockDao.selectLowStockWarning();
    }

    @Override
    public int updateStockStatusBatch(List<Long> skuIds, Integer stockStatus) {
        if (skuIds == null || skuIds.isEmpty()) {
            return 0;
        }
        if (stockStatus == null || stockStatus < 0 || stockStatus > 2) {
            throw new ApiException("库存状态值必须为0-2");
        }
        return skuStockDao.updateStockStatusBatch(skuIds, stockStatus);
    }
}

