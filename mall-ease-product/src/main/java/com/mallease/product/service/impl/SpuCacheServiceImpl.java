package com.mallease.product.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.product.cache.ProductDetailCacheService;
import com.mallease.product.cache.ProductStockCacheService;
import com.mallease.product.converter.SpuCacheConverter;
import com.mallease.product.model.data.cache.SpuCache;
import com.mallease.product.model.data.entity.*;
import com.mallease.product.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SPU缓存服务实现
 *
 * @author: Aulen
 * @create: 2025-12-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpuCacheServiceImpl implements SpuCacheService {

    private final ProductDetailCacheService productDetailCacheService;
    private final ProductStockCacheService productStockCacheService;
    private final SpuService spuService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final SkuService skuService;
    private final SkuStockService skuStockService;
    private final SpuCacheConverter cacheConverter;

    // 缓存预热

    @Override
    public void warmUp(Long spuId) {
        if (spuId == null) {
            log.warn("预热缓存失败：spuId 为 null");
            return;
        }

        warmUpBatch(Collections.singletonList(spuId));
    }

    @Override
    public void warmUpBatch(List<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return;
        }

        log.info("开始批量预热SPU缓存，数量: {}", spuIds.size());
        try {
            // 批量查询SPU详情数据
            List<SpuCache> cacheDTOList = buildSpuCacheBatch(spuIds);
            if (CollUtil.isEmpty(cacheDTOList)) {
                log.warn("未查询到SPU数据，spuIds: {}", spuIds);
                return;
            }
            persistCaches(cacheDTOList);

            log.info("批量预热SPU缓存完成，成功: {} 个", cacheDTOList.size());

        } catch (Exception e) {
            log.error("批量预热SPU缓存失败，spuIds: {}", spuIds, e);
            throw new RuntimeException("缓存预热失败", e);
        }
    }

    @Override
    public SpuCache get(Long spuId) {
        if (spuId == null) {
            return null;
        }
        return productDetailCacheService.get(spuId);
    }

    @Override
    public SpuCache loadAndCache(Long spuId) {
        if (spuId == null) {
            return null;
        }

        Map<Long, SpuCache> cacheMap = loadAndCacheBatch(Collections.singletonList(spuId));
        return cacheMap.get(spuId);
    }

    @Override
    public Map<Long, SpuCache> getBatch(List<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return Collections.emptyMap();
        }

        return productDetailCacheService.batchGet(spuIds);
    }

    @Override
    public Map<Long, SpuCache> loadAndCacheBatch(List<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return Collections.emptyMap();
        }

        List<Long> distinctSpuIds = spuIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (distinctSpuIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<SpuCache> cacheDTOList = buildSpuCacheBatch(distinctSpuIds);
        if (CollUtil.isEmpty(cacheDTOList)) {
            log.warn("数据库构建SPU失败，spuIds: {}", distinctSpuIds);
            return Collections.emptyMap();
        }

        persistCaches(cacheDTOList);
        return cacheDTOList.stream().collect(Collectors.toMap(SpuCache::getId, cache -> cache));
    }

    @Override
    public Integer getSkuStock(Long spuId, Long skuId) {
        if (spuId == null || skuId == null) {
            return null;
        }
        return productStockCacheService.getStock(spuId, skuId);
    }

    @Override
    public Map<Long, Integer> getSkuStockBySpu(Long spuId) {
        return productStockCacheService.getStockBySpu(spuId);
    }

    @Override
    public Map<Long, Integer> getSkuStockBatch(List<SkuStockQueryDTO> skuQueries) {
        return productStockCacheService.batchGet(skuQueries);
    }

    @Override
    public void setSkuStockBatch(Map<Long, Map<Long, Integer>> skuStockMap) {
        productStockCacheService.batchSet(skuStockMap);
    }

    @Override
    public void evict(Long spuId) {
        if (spuId == null) {
            return;
        }

        evictBatch(Collections.singletonList(spuId));
    }

    @Override
    public void evictBatch(List<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return;
        }

        try {
            productDetailCacheService.deleteBatch(spuIds);
            productStockCacheService.deleteBatch(spuIds);
            log.info("批量删除SPU缓存完成，数量: {}", spuIds.size());
        } catch (Exception e) {
            log.error("批量删除SPU缓存失败，spuIds: {}", spuIds, e);
        }
    }

    // 库存操作

    @Override
    public Long decreaseStock(Long spuId, Long skuId, int quantity) {
        if (spuId == null || skuId == null || quantity <= 0) {
            log.warn("扣减库存参数无效，spuId: {}, skuId: {}, quantity: {}", spuId, skuId, quantity);
            return null;
        }

        Long result = productStockCacheService.deduct(spuId, skuId, quantity);

        if (result == null) {
            log.error("SKU库存扣减系统异常，spuId: {}, skuId: {}", spuId, skuId);
            return null;
        }

        if (result == ProductStockCacheService.STOCK_CACHE_MISS) {
            log.info("缓存不存在，触发回源重建，spuId: {}", spuId);
            try {
                warmUpBatch(Collections.singletonList(spuId));
                result = productStockCacheService.deduct(spuId, skuId, quantity);
                if (result == null || result < 0) {
                    log.warn("回源后扣减仍失败，spuId: {}, skuId: {}, result: {}", spuId, skuId, result);
                    return null;
                }
                log.info("回源后扣减成功，spuId: {}, skuId: {}, 剩余: {}", spuId, skuId, result);
                return result;
            } catch (Exception e) {
                log.error("回源重建失败，spuId: {}", spuId, e);
                return null;
            }
        }

        if (result == ProductStockCacheService.STOCK_NOT_ENOUGH) {
            log.warn("SKU库存不足，spuId: {}, skuId: {}, 尝试扣减: {}", spuId, skuId, quantity);
            return null;
        }

        log.debug("SKU库存扣减成功，spuId: {}, skuId: {}, 扣减: {}, 剩余: {}",
                spuId, skuId, quantity, result);
        return result;
    }

    @Override
    public Long increaseStock(Long spuId, Long skuId, int quantity) {
        if (spuId == null || skuId == null || quantity <= 0) {
            log.warn("恢复库存参数无效，spuId: {}, skuId: {}, quantity: {}", spuId, skuId, quantity);
            return null;
        }

        Long newStock = productStockCacheService.release(spuId, skuId, quantity);
        if (newStock == null) {
            log.info("恢复库存时缓存缺失，触发回源重建，spuId: {}", spuId);
            warmUpBatch(Collections.singletonList(spuId));
            newStock = productStockCacheService.release(spuId, skuId, quantity);
        }
        if (newStock != null) {
            log.debug("SKU库存恢复成功，spuId: {}, skuId: {}, 恢复: {}, 当前: {}",
                    spuId, skuId, quantity, newStock);
        }

        return newStock;
    }

    @Override
    public boolean decreaseStockBatch(Map<Long, Map<Long, Integer>> stockDecrements) {
        if (CollUtil.isEmpty(stockDecrements)) {
            return true;
        }

        try {
            for (Map.Entry<Long, Map<Long, Integer>> entry : stockDecrements.entrySet()) {
                Long spuId = entry.getKey();
                Map<Long, Integer> skuQuantities = entry.getValue();
                for (Map.Entry<Long, Integer> skuEntry : skuQuantities.entrySet()) {
                    Long skuId = skuEntry.getKey();
                    Integer quantity = skuEntry.getValue();
                    Long newStock = decreaseStock(spuId, skuId, quantity);
                    if (newStock == null) {
                        log.error("批量扣减库存失败，spuId: {}, skuId: {}, quantity: {}",
                                spuId, skuId, quantity);
                        return false;
                    }
                }
            }

            return true;

        } catch (Exception e) {
            log.error("批量扣减库存失败", e);
            return false;
        }
    }

    /**
     * 批量构建SPU缓存对象
     */
    private List<SpuCache> buildSpuCacheBatch(List<Long> spuIds) {

        List<Spu> spuList = spuService.listByIds(spuIds);
        if (CollUtil.isEmpty(spuList)) {
            return Collections.emptyList();
        }

        List<SpuDetail> detailList = spuService.listDetailBySpuIds(spuIds);
        Map<Long, SpuDetail> detailMap = detailList.stream()
                .collect(Collectors.toMap(SpuDetail::getSpuId, detail -> detail));

        Set<Long> brandIds = spuList.stream()
                .map(Spu::getBrandId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Brand> brandMap = CollUtil.isEmpty(brandIds) ? Collections.emptyMap() :
                brandService.listByIds(new ArrayList<>(brandIds)).stream()
                        .collect(Collectors.toMap(Brand::getId, brand -> brand));

        Set<Long> categoryIds = spuList.stream()
                .map(Spu::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Category> categoryMap = CollUtil.isEmpty(categoryIds) ? Collections.emptyMap() :
                categoryService.listByIds(new ArrayList<>(categoryIds)).stream()
                        .collect(Collectors.toMap(Category::getId, category -> category));

        List<Sku> skuList = skuService.selectBySpuIds(spuIds);
        Map<Long, List<Sku>> skuGroupMap = skuList.stream()
                .collect(Collectors.groupingBy(Sku::getSpuId));
        Set<Long> skuIds = skuList.stream()
                .map(Sku::getId)
                .collect(Collectors.toSet());
        Map<Long, SkuStock> stockMap = CollUtil.isEmpty(skuIds) ? Collections.emptyMap() :
                skuStockService.listStockBySpuIds(spuIds).stream()
                        .collect(Collectors.toMap(SkuStock::getSkuId, stock -> stock));

        Map<Long, SkuPromotion> promotionMap = CollUtil.isEmpty(skuIds) ? Collections.emptyMap() :
                skuService.listPromotionBySkuIds(new ArrayList<>(skuIds)).stream()
                        .collect(Collectors.toMap(SkuPromotion::getSkuId, promotion -> promotion));

        List<SpuFullReduction> reductionList = spuService.listFullReductionBySpuIds(spuIds);
        Map<Long, List<SpuFullReduction>> reductionGroupMap = reductionList.stream()
                .collect(Collectors.groupingBy(SpuFullReduction::getSpuId));

        List<SpuCache> cacheDTOList = new ArrayList<>();
        long cacheTime = System.currentTimeMillis();
        for (Spu spu : spuList) {
            SpuDetail detail = detailMap.get(spu.getId());
            Brand brand = brandMap.get(spu.getBrandId());
            Category category = categoryMap.get(spu.getCategoryId());
            List<Sku> spuSkuList = skuGroupMap.getOrDefault(spu.getId(), Collections.emptyList());
            List<SpuFullReduction> spuReductions = reductionGroupMap.getOrDefault(spu.getId(), Collections.emptyList());
            SpuCache cacheDTO = buildSingleSpuCache(
                    spu, detail, brand, category, spuSkuList, stockMap, promotionMap, spuReductions, cacheTime
            );
            cacheDTOList.add(cacheDTO);
        }

        return cacheDTOList;
    }

    private void persistCaches(List<SpuCache> cacheDTOList) {
        productDetailCacheService.batchSet(cacheDTOList);

        List<Long> allSkuIds = cacheDTOList.stream()
                .flatMap(dto -> dto.getSkuList().stream())
                .map(sku -> sku.getBasic().getId())
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(allSkuIds)) {
            return;
        }

        List<SkuStock> stockList = skuStockService.listStockBySpuIds(
                cacheDTOList.stream().map(SpuCache::getId).collect(Collectors.toList())
        );
        Map<Long, SkuStock> stockMap = stockList.stream()
                .collect(Collectors.toMap(SkuStock::getSkuId, stock -> stock));

        Map<Long, Map<String, Integer>> skuStockMap = cacheDTOList.stream()
                .collect(Collectors.toMap(
                        SpuCache::getId,
                        dto -> dto.getSkuList().stream()
                                .collect(Collectors.toMap(
                                        sku -> String.valueOf(sku.getBasic().getId()),
                                        sku -> {
                                            SkuStock stock = stockMap.get(sku.getBasic().getId());
                                            return stock != null ? stock.getStock() : 0;
                                        }
                                ))
                ));
        Map<Long, Map<Long, Integer>> normalizedStockMap = skuStockMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        stockEntry -> Long.valueOf(stockEntry.getKey()),
                                        Map.Entry::getValue
                                ))
                ));
        productStockCacheService.batchSet(normalizedStockMap);
    }

    private SpuCache buildSingleSpuCache(Spu spu,
                                         SpuDetail detail,
                                         Brand brand,
                                         Category category,
                                         List<Sku> skuList,
                                         Map<Long, SkuStock> stockMap,
                                         Map<Long, SkuPromotion> promotionMap,
                                         List<SpuFullReduction> reductionList,
                                         long cacheTime) {

        SpuCache.SpuBasicInfo spuBasic = cacheConverter.toSpuBasicInfo(spu);

        SpuCache.SpuDetailInfo spuDetail = detail != null
                ? cacheConverter.toSpuDetailInfo(detail, spu)
                : null;

        SpuCache.BrandInfo brandInfo = brand != null
                ? cacheConverter.toBrandInfo(brand)
                : null;

        SpuCache.CategoryInfo categoryInfo = category != null
                ? cacheConverter.toCategoryInfo(category, spu.getCategoryIds())
                : null;

        List<SpuCache.SkuInfo> skuInfoList = skuList.stream()
                .map(sku -> {
                    SkuStock stock = stockMap.get(sku.getId());
                    SkuPromotion promotion = promotionMap.get(sku.getId());
                    return SpuCache.SkuInfo.builder()
                            .basic(cacheConverter.toSkuBasicInfo(sku))
                            .price(cacheConverter.toSkuPriceInfo(sku))
                            .promotion(promotion != null ? cacheConverter.toSkuPromotionInfo(promotion) : null)
                            .benefit(promotion != null ? cacheConverter.toSkuBenefitInfo(promotion) : null)
                            .config(stock != null ? cacheConverter.toSkuConfigInfo(stock) : null)
                            .build();
                })
                .collect(Collectors.toList());

        List<SpuCache.FullReductionInfo> fullReductionInfoList =
                cacheConverter.toFullReductionInfoList(reductionList);

        return SpuCache.builder()
                .spuBasic(spuBasic)
                .spuDetail(spuDetail)
                .brand(brandInfo)
                .category(categoryInfo)
                .skuList(skuInfoList)
                .fullReductionList(fullReductionInfoList)
                .cacheTime(cacheTime)
                .version(spu.getVersion())
                .build();
    }
}
