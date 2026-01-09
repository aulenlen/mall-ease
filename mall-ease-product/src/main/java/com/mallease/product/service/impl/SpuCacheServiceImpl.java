package com.mallease.product.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.mallease.product.component.CacheService;
import com.mallease.product.constant.RedisKey;
import com.mallease.product.converter.SpuCacheConverter;
import com.mallease.product.model.data.cache.SpuCache;
import com.mallease.product.model.data.entity.*;
import com.mallease.product.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SpuCacheServiceImpl implements SpuCacheService {

    @Autowired
    private CacheService cacheService;
    @Autowired
    private SpuService spuService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private SkuStockService skuStockService;
    @Autowired
    private SpuCacheConverter cacheConverter;

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

            // 批量写入SPU详情缓存（String结构）
            cacheService.batchSetList(
                    cacheDTOList,
                    RedisKey.SPU_DETAIL.getPrefix(),
                    SpuCache::getId,
                    RedisKey.SPU_DETAIL.getTtl()
            );

            // 批量写入SKU库存缓存（Hash结构）
            List<Long> allSkuIds = cacheDTOList.stream()
                    .flatMap(dto -> dto.getSkuList().stream())
                    .map(sku -> sku.getBasic().getId())
                    .collect(Collectors.toList());
            if (!CollUtil.isEmpty(allSkuIds)) {
                List<SkuStock> stockList = skuStockService.listStockBySpuIds(
                        cacheDTOList.stream().map(SpuCache::getId).collect(Collectors.toList())
                );
                Map<Long, SkuStock> stockMap = stockList.stream()
                        .collect(Collectors.toMap(SkuStock::getSkuId, stock -> stock));

                // 按SPU分组
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
                cacheService.batchSetHashMap(
                        RedisKey.SPU_SKU_STOCK.getPrefix(),
                        skuStockMap,
                        RedisKey.SPU_SKU_STOCK.getTtl()
                );
            }

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
        return cacheService.get(RedisKey.SPU_DETAIL, spuId, SpuCache.class);
    }

    @Override
    public Map<Long, SpuCache> getBatch(List<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return Collections.emptyMap();
        }

        return cacheService.batchGet(spuIds, RedisKey.SPU_DETAIL.getPrefix(), SpuCache.class);
    }

    @Override
    public Integer getSkuStock(Long spuId, Long skuId) {
        if (spuId == null || skuId == null) {
            return null;
        }

        String hashKey = String.valueOf(skuId);
        Object value = cacheService.hGet(RedisKey.SPU_SKU_STOCK, spuId, hashKey);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }

    @Override
    public Map<Long, Integer> getSkuStockBySpu(Long spuId) {
        if (spuId == null) {
            return Collections.emptyMap();
        }

        Map<Object, Object> hashMap = cacheService.hGetAll(RedisKey.SPU_SKU_STOCK, spuId);
        if (CollUtil.isEmpty(hashMap)) {
            return Collections.emptyMap();
        }

        return hashMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> Long.valueOf(entry.getKey().toString()),
                        entry -> (Integer) entry.getValue()
                ));
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
            // 删除SPU详情缓存
            cacheService.deleteBatch(spuIds, RedisKey.SPU_DETAIL.getPrefix());

            // 删除SKU库存缓存
            cacheService.deleteBatch(spuIds, RedisKey.SPU_SKU_STOCK.getPrefix());
            log.info("批量删除SPU缓存完成，数量: {}", spuIds.size());

        } catch (Exception e) {
            log.error("批量删除SPU缓存失败，spuIds: {}", spuIds, e);
        }
    }

    // ==================== 库存操作 ====================

    @Override
    public Long decreaseStock(Long spuId, Long skuId, int quantity) {
        if (spuId == null || skuId == null || quantity <= 0) {
            log.warn("扣减库存参数无效，spuId: {}, skuId: {}, quantity: {}", spuId, skuId, quantity);
            return null;
        }

        String hashKey = String.valueOf(skuId);
        Long newStock = cacheService.deductStockAtomic(RedisKey.SPU_SKU_STOCK, spuId, hashKey, quantity);
        if (newStock == null) {
            log.warn("SKU库存不足或扣减失败，spuId: {}, skuId: {}, 尝试扣减: {}", spuId, skuId, quantity);
            return null;
        }

        log.debug("SKU库存扣减成功，spuId: {}, skuId: {}, 扣减: {}, 剩余: {}",
                spuId, skuId, quantity, newStock);
        return newStock;
    }

    @Override
    public Long increaseStock(Long spuId, Long skuId, int quantity) {
        if (spuId == null || skuId == null || quantity <= 0) {
            log.warn("恢复库存参数无效，spuId: {}, skuId: {}, quantity: {}", spuId, skuId, quantity);
            return null;
        }

        String hashKey = String.valueOf(skuId);
        Long newStock = cacheService.hIncr(RedisKey.SPU_SKU_STOCK, spuId, hashKey, quantity);
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

        // 批量查询SPU基础信息
        List<Spu> spuList = spuService.listByIds(spuIds);
        if (CollUtil.isEmpty(spuList)) {
            return Collections.emptyList();
        }

        // 批量查询SPU详情
        List<SpuDetail> detailList = spuService.listDetailBySpuIds(spuIds);
        Map<Long, SpuDetail> detailMap = detailList.stream()
                .collect(Collectors.toMap(SpuDetail::getSpuId, detail -> detail));

        // 批量查询品牌
        Set<Long> brandIds = spuList.stream()
                .map(Spu::getBrandId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Brand> brandMap = CollUtil.isEmpty(brandIds) ? Collections.emptyMap() :
                brandService.listByIds(new ArrayList<>(brandIds)).stream()
                        .collect(Collectors.toMap(Brand::getId, brand -> brand));

        // 批量查询分类
        Set<Long> categoryIds = spuList.stream()
                .map(Spu::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Category> categoryMap = CollUtil.isEmpty(categoryIds) ? Collections.emptyMap() :
                categoryService.listByIds(new ArrayList<>(categoryIds)).stream()
                        .collect(Collectors.toMap(Category::getId, category -> category));

        // 批量查询SKU和库存
        List<Sku> skuList = skuService.selectBySpuIds(spuIds);
        Map<Long, List<Sku>> skuGroupMap = skuList.stream()
                .collect(Collectors.groupingBy(Sku::getSpuId));
        Set<Long> skuIds = skuList.stream()
                .map(Sku::getId)
                .collect(Collectors.toSet());
        Map<Long, SkuStock> stockMap = CollUtil.isEmpty(skuIds) ? Collections.emptyMap() :
                skuStockService.listStockBySpuIds(spuIds).stream()
                        .collect(Collectors.toMap(SkuStock::getSkuId, stock -> stock));

        // 批量查询SKU促销信息
        Map<Long, SkuPromotion> promotionMap = CollUtil.isEmpty(skuIds) ? Collections.emptyMap() :
                skuService.listPromotionBySkuIds(new ArrayList<>(skuIds)).stream()
                        .collect(Collectors.toMap(SkuPromotion::getSkuId, promotion -> promotion));

        // 批量查询满减规则
        List<SpuFullReduction> reductionList = spuService.listFullReductionBySpuIds(spuIds);
        Map<Long, List<SpuFullReduction>> reductionGroupMap = reductionList.stream()
                .collect(Collectors.groupingBy(SpuFullReduction::getSpuId));

        // 组装缓存对象
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

    private SpuCache buildSingleSpuCache(Spu spu,
                                         SpuDetail detail,
                                         Brand brand,
                                         Category category,
                                         List<Sku> skuList,
                                         Map<Long, SkuStock> stockMap,
                                         Map<Long, SkuPromotion> promotionMap,
                                         List<SpuFullReduction> reductionList,
                                         long cacheTime) {

        // 1. 构建SPU基础信息
        SpuCache.SpuBasicInfo spuBasic = cacheConverter.toSpuBasicInfo(spu);

        // 2. 构建SPU详情信息
        SpuCache.SpuDetailInfo spuDetail = detail != null
                ? cacheConverter.toSpuDetailInfo(detail, spu)
                : null;

        // 3. 构建品牌信息
        SpuCache.BrandInfo brandInfo = brand != null
                ? cacheConverter.toBrandInfo(brand)
                : null;

        // 4. 构建分类信息
        SpuCache.CategoryInfo categoryInfo = category != null
                ? cacheConverter.toCategoryInfo(category, spu.getCategoryIds())
                : null;

        // 5. 构建SKU列表
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

        // 6. 构建满减规则列表
        List<SpuCache.FullReductionInfo> fullReductionInfoList =
                cacheConverter.toFullReductionInfoList(reductionList);

        // 7. 组装聚合根
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
