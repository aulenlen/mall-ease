package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.dao.PmsSkuStockDao;
import com.mallease.pms.pojo.PmsSkuStock;
import com.mallease.pms.service.PmsSkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * SKU库存服务实现类
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
@Slf4j
@Service
public class PmsSkuStockServiceImpl implements PmsSkuStockService {
    @Autowired
    private PmsSkuStockDao skuStockDao;

    @Override
    public List<PmsSkuStock> getByProductIdAndKeyword(Long productId, String keyword) {
        log.info("查询SKU库存, productId: {}, keyword: {}", productId, keyword);
        return skuStockDao.selectByProductIdAndKeyword(productId, keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(Long productId, List<PmsSkuStock> skuStockList) {
        log.info("批量更新SKU库存, productId: {}, 更新数量: {}", productId, skuStockList.size());

        if (CollectionUtils.isEmpty(skuStockList)) {
            throw new ApiException("SKU库存列表不能为空");
        }

        // 验证所有SKU的ID不为空
        for (PmsSkuStock skuStock : skuStockList) {
            if (skuStock.getId() == null) {
                throw new ApiException("SKU ID不能为空");
            }
        }

        // 一次性查询该商品的所有SKU库存
        List<PmsSkuStock> existingSkuList = skuStockDao.selectByProductId(productId);
        if (CollectionUtils.isEmpty(existingSkuList)) {
            throw new ApiException("该商品没有SKU库存信息");
        }

        // 构建SKU ID集合，用于快速验证
        java.util.Set<Long> validSkuIds = existingSkuList.stream()
                .map(PmsSkuStock::getId)
                .collect(java.util.stream.Collectors.toSet());

        // 验证所有待更新的SKU是否属于该商品
        for (PmsSkuStock skuStock : skuStockList) {
            if (!validSkuIds.contains(skuStock.getId())) {
                throw new ApiException("SKU不属于该商品: " + skuStock.getId());
            }
            // 确保更新时不改变产品ID
            skuStock.setProductId(productId);
        }

        return skuStockDao.updateBatchSelective(skuStockList);
    }
}

