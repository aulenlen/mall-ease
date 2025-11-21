package com.mallease.pms.component;

import com.mallease.common.constant.PmsRedisKeys;
import com.mallease.common.service.RedisService;
import com.mallease.pms.dto.cache.PmsProductDetailCacheDTO;
import com.mallease.pms.service.PmsProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品缓存组件（高性能版本）
 * 使用扩展后的 RedisService 进行批量操作
 *
 * @author: Aulen
 * @create: 2025-11-21
 */
@Component
@Slf4j
public class CacheComponent {

    @Autowired
    private RedisService redisService;

    @Autowired
    private PmsProductService productService;

    /**
     * 批量获取商品详情（带缓存）
     *
     * @param productIds 商品ID列表
     * @return 商品详情列表
     */
    public List<PmsProductDetailCacheDTO> getProductDetailBatchWithCache(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量从 Redis 获取
        Map<Long, PmsProductDetailCacheDTO> cachedMap = batchGetFromCache(productIds);
        log.info("从缓存获取到 {} 个商品", cachedMap.size());

        // 2.找出未命中的商品ID
        List<Long> missedIds = productIds.stream()
                .filter(id -> !cachedMap.containsKey(id))
                .toList();

        // 3. 从数据库查询未命中的数据
        if (!missedIds.isEmpty()) {
            log.info("缓存未命中 {} 个商品，从数据库查询", missedIds.size());
            List<PmsProductDetailCacheDTO> fromDb = productService.getProductDetailBatch(missedIds);

            // 4.批量写入缓存
            batchSetToCache(fromDb);

            // 5. 合并结果
            fromDb.forEach(dto -> cachedMap.put(dto.getProduct().getId(), dto));
        }

        // 6. 按原始顺序返回
        return productIds.stream()
                .map(cachedMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 批量从缓存获取
     */
    private Map<Long, PmsProductDetailCacheDTO> batchGetFromCache(List<Long> productIds) {
        // 构建 key 列表
        List<String> keys = productIds.stream()
                .map(id -> PmsRedisKeys.PRODUCT_DETAIL_PREFIX + id)
                .toList();

        List<Object> values = redisService.multiGet(keys);

        Map<Long, PmsProductDetailCacheDTO> result = new HashMap<>();
        if (values != null) {
            for (int i = 0; i < productIds.size(); i++) {
                Object value = values.get(i);
                if (value instanceof PmsProductDetailCacheDTO) {
                    result.put(productIds.get(i), (PmsProductDetailCacheDTO) value);
                }
            }
        }

        return result;
    }

    /**
     * 批量写入缓存
     */
    private void batchSetToCache(List<PmsProductDetailCacheDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return;
        }

        // 构建 key-value map
        Map<String, Object> cacheMap = dtoList.stream()
                .collect(Collectors.toMap(
                        dto -> PmsRedisKeys.PRODUCT_DETAIL_PREFIX + dto.getProduct().getId(),
                        dto -> dto
                ));

        // 【使用扩展方法】批量写入（1次网络往返）
        redisService.multiSetWithExpire(cacheMap, PmsRedisKeys.getProductDetailCacheExpireSeconds());

        log.info("批量写入缓存 {} 个商品", dtoList.size());
    }

    /**
     * 删除缓存
     */
    public void deleteCache(Long productId) {
        String key = PmsRedisKeys.PRODUCT_DETAIL_PREFIX + productId;
        redisService.del(key);
        log.info("删除商品缓存，商品ID: {}", productId);
    }

    /**
     * 批量删除缓存
     */
    public void deleteCacheBatch(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        List<String> keys = productIds.stream()
                .map(id -> PmsRedisKeys.PRODUCT_DETAIL_PREFIX + id)
                .toList();

        redisService.del(keys);
        log.info("批量删除商品缓存 {} 个", productIds.size());
    }

    /**
     * 预热缓存（在商品上架后调用）
     */
    public void warmUpCache(List<Long> productIds) {
        log.info("开始预热商品缓存，商品数量: {}", productIds.size());

        List<PmsProductDetailCacheDTO> dtoList = productService.getProductDetailBatch(productIds);
        batchSetToCache(dtoList);

        log.info("商品缓存预热完成");
    }
}
