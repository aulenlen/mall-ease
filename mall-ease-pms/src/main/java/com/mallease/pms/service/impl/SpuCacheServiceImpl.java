package com.mallease.pms.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.mallease.common.constant.PmsRedisKeys;
import com.mallease.common.service.RedisService;
import com.mallease.pms.component.CacheService;
import com.mallease.pms.converter.PmsSpuCacheConverter;
import com.mallease.pms.dao.*;
import com.mallease.pms.dto.cache.PmsSpuCacheDTO;
import com.mallease.pms.pojo.*;
import com.mallease.pms.service.SpuCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SPU缓存服务实现
 * <p>
 * 职责：
 * 1. 管理SPU详情和SKU库存的Redis缓存
 * 2. 提供缓存预热、查询、失效等操作
 * 3. 支持原子化的库存扣减/恢复
 *
 * @author: Aulen
 * @create: 2025-12-22
 */
@Slf4j
@Service
public class SpuCacheServiceImpl implements SpuCacheService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private PmsSpuDao spuDao;

    @Autowired
    private PmsSpuDetailDao spuDetailDao;

    @Autowired
    private PmsBrandDao brandDao;

    @Autowired
    private PmsCategoryDao categoryDao;

    @Autowired
    private PmsSkuDao skuDao;

    @Autowired
    private PmsSkuStockDao skuStockDao;

    @Autowired
    private PmsSpuFullReductionDao fullReductionDao;

    @Autowired
    private PmsSkuPromotionDao skuPromotionDao;

    @Autowired
    private PmsSpuCacheConverter cacheConverter;

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
            List<PmsSpuCacheDTO> cacheDTOList = buildSpuCacheBatch(spuIds);

            if (CollUtil.isEmpty(cacheDTOList)) {
                log.warn("未查询到SPU数据，spuIds: {}", spuIds);
                return;
            }

            // 批量写入SPU详情缓存（String结构）
            cacheService.batchSetList(
                    cacheDTOList,
                    PmsRedisKeys.SPU_DETAIL_PREFIX,
                    PmsSpuCacheDTO::getId,
                    PmsRedisKeys.getSpuDetailCacheExpireSeconds()
            );

            // 批量写入SKU库存缓存（Hash结构）
            List<Long> allSkuIds = cacheDTOList.stream()
                    .flatMap(dto -> dto.getSkuList().stream())
                    .map(sku -> sku.getBasic().getId())
                    .collect(Collectors.toList());

            if (!CollUtil.isEmpty(allSkuIds)) {
                List<PmsSkuStock> stockList = skuStockDao.selectBySkuIds(allSkuIds);
                Map<Long, PmsSkuStock> stockMap = stockList.stream()
                        .collect(Collectors.toMap(PmsSkuStock::getSkuId, stock -> stock));

                // 按SPU分组
                Map<Long, Map<String, Integer>> skuStockMap = cacheDTOList.stream()
                        .collect(Collectors.toMap(
                                PmsSpuCacheDTO::getId,
                                dto -> dto.getSkuList().stream()
                                        .collect(Collectors.toMap(
                                                sku -> String.valueOf(sku.getBasic().getId()),
                                                sku -> {
                                                    PmsSkuStock stock = stockMap.get(sku.getBasic().getId());
                                                    return stock != null ? stock.getStock() : 0;
                                                }
                                        ))
                        ));

                cacheService.batchSetHashMap(
                        PmsRedisKeys.SPU_SKU_STOCK_PREFIX,
                        skuStockMap,
                        PmsRedisKeys.getSkuStockCacheExpireSeconds()
                );
            }

            log.info("批量预热SPU缓存完成，成功: {} 个", cacheDTOList.size());

        } catch (Exception e) {
            log.error("批量预热SPU缓存失败，spuIds: {}", spuIds, e);
            throw new RuntimeException("缓存预热失败", e);
        }
    }

    @Override
    public PmsSpuCacheDTO get(Long spuId) {
        if (spuId == null) {
            return null;
        }

        try {
            String key = PmsRedisKeys.spuDetailKey(spuId);
            Object value = redisService.get(key);

            if (value instanceof PmsSpuCacheDTO) {
                return (PmsSpuCacheDTO) value;
            }

            return null;

        } catch (Exception e) {
            log.error("获取SPU缓存失败，spuId: {}", spuId, e);
            return null;
        }
    }

    @Override
    public Map<Long, PmsSpuCacheDTO> getBatch(List<Long> spuIds) {
        if (CollUtil.isEmpty(spuIds)) {
            return Collections.emptyMap();
        }

        return cacheService.batchGet(spuIds, PmsRedisKeys.SPU_DETAIL_PREFIX, PmsSpuCacheDTO.class);
    }

    @Override
    public Integer getSkuStock(Long spuId, Long skuId) {
        if (spuId == null || skuId == null) {
            return null;
        }

        try {
            String key = PmsRedisKeys.spuSkuStockKey(spuId);
            String hashKey = String.valueOf(skuId);
            Object value = redisService.hGet(key, hashKey);

            if (value instanceof Integer) {
                return (Integer) value;
            }

            return null;

        } catch (Exception e) {
            log.error("获取SKU库存缓存失败，spuId: {}, skuId: {}", spuId, skuId, e);
            return null;
        }
    }

    @Override
    public Map<Long, Integer> getSkuStockBySpu(Long spuId) {
        if (spuId == null) {
            return Collections.emptyMap();
        }

        try {
            String key = PmsRedisKeys.spuSkuStockKey(spuId);
            Map<Object, Object> hashMap = redisService.hGetAll(key);

            if (CollUtil.isEmpty(hashMap)) {
                return Collections.emptyMap();
            }

            return hashMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> Long.valueOf(entry.getKey().toString()),
                            entry -> (Integer) entry.getValue()
                    ));

        } catch (Exception e) {
            log.error("批量获取SKU库存缓存失败，spuId: {}", spuId, e);
            return Collections.emptyMap();
        }
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
            cacheService.deleteBatch(spuIds, PmsRedisKeys.SPU_DETAIL_PREFIX);

            // 删除SKU库存缓存
            cacheService.deleteBatch(spuIds, PmsRedisKeys.SPU_SKU_STOCK_PREFIX);

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

        try {
            String key = PmsRedisKeys.spuSkuStockKey(spuId);
            String hashKey = String.valueOf(skuId);

            // 使用 HINCRBY 原子扣减
            Long newStock = redisService.hIncr(key, hashKey, (long) -quantity);

            if (newStock < 0) {
                // 库存不足，回滚
                redisService.hIncr(key, hashKey, (long) quantity);
                log.warn("SKU库存不足，spuId: {}, skuId: {}, 当前库存: {}, 扣减数量: {}",
                        spuId, skuId, newStock + quantity, quantity);
                return null;
            }

            log.debug("SKU库存扣减成功，spuId: {}, skuId: {}, 扣减: {}, 剩余: {}",
                    spuId, skuId, quantity, newStock);

            return newStock;

        } catch (Exception e) {
            log.error("扣减SKU库存失败，spuId: {}, skuId: {}, quantity: {}", spuId, skuId, quantity, e);
            return null;
        }
    }

    @Override
    public Long increaseStock(Long spuId, Long skuId, int quantity) {
        if (spuId == null || skuId == null || quantity <= 0) {
            log.warn("恢复库存参数无效，spuId: {}, skuId: {}, quantity: {}", spuId, skuId, quantity);
            return null;
        }

        try {
            String key = PmsRedisKeys.spuSkuStockKey(spuId);
            String hashKey = String.valueOf(skuId);

            // 使用 HINCRBY 原子增加
            Long newStock = redisService.hIncr(key, hashKey, (long) quantity);

            log.debug("SKU库存恢复成功，spuId: {}, skuId: {}, 恢复: {}, 当前: {}",
                    spuId, skuId, quantity, newStock);

            return newStock;

        } catch (Exception e) {
            log.error("恢复SKU库存失败，spuId: {}, skuId: {}, quantity: {}", spuId, skuId, quantity, e);
            return null;
        }
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
    private List<PmsSpuCacheDTO> buildSpuCacheBatch(List<Long> spuIds) {
        // 批量查询SPU基础信息
        List<PmsSpu> spuList = spuDao.selectByIds(spuIds);
        if (CollUtil.isEmpty(spuList)) {
            return Collections.emptyList();
        }

        // 批量查询SPU详情
        List<PmsSpuDetail> detailList = spuDetailDao.selectBySpuIds(spuIds);
        Map<Long, PmsSpuDetail> detailMap = detailList.stream()
                .collect(Collectors.toMap(PmsSpuDetail::getSpuId, detail -> detail));

        // 批量查询品牌
        Set<Long> brandIds = spuList.stream()
                .map(PmsSpu::getBrandId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, PmsBrand> brandMap = CollUtil.isEmpty(brandIds) ? Collections.emptyMap() :
                brandDao.selectByIds(new ArrayList<>(brandIds)).stream()
                        .collect(Collectors.toMap(PmsBrand::getId, brand -> brand));

        // 批量查询分类
        Set<Long> categoryIds = spuList.stream()
                .map(PmsSpu::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, PmsCategory> categoryMap = CollUtil.isEmpty(categoryIds) ? Collections.emptyMap() :
                categoryDao.selectByIds(new ArrayList<>(categoryIds)).stream()
                        .collect(Collectors.toMap(PmsCategory::getId, category -> category));

        // 批量查询SKU和库存
        List<PmsSku> skuList = skuDao.selectBySpuIds(spuIds);
        Map<Long, List<PmsSku>> skuGroupMap = skuList.stream()
                .collect(Collectors.groupingBy(PmsSku::getSpuId));

        Set<Long> skuIds = skuList.stream()
                .map(PmsSku::getId)
                .collect(Collectors.toSet());
        Map<Long, PmsSkuStock> stockMap = CollUtil.isEmpty(skuIds) ? Collections.emptyMap() :
                skuStockDao.selectBySkuIds(new ArrayList<>(skuIds)).stream()
                        .collect(Collectors.toMap(PmsSkuStock::getSkuId, stock -> stock));

        // 批量查询SKU促销信息
        Map<Long, PmsSkuPromotion> promotionMap = CollUtil.isEmpty(skuIds) ? Collections.emptyMap() :
                skuPromotionDao.selectBySkuIds(new ArrayList<>(skuIds)).stream()
                        .collect(Collectors.toMap(PmsSkuPromotion::getSkuId, promotion -> promotion));

        // 批量查询满减规则
        List<PmsSpuFullReduction> reductionList = fullReductionDao.selectBySpuIds(spuIds);
        Map<Long, List<PmsSpuFullReduction>> reductionGroupMap = reductionList.stream()
                .collect(Collectors.groupingBy(PmsSpuFullReduction::getSpuId));

        // 组装缓存对象
        List<PmsSpuCacheDTO> cacheDTOList = new ArrayList<>();
        long cacheTime = System.currentTimeMillis();

        for (PmsSpu spu : spuList) {
            PmsSpuDetail detail = detailMap.get(spu.getId());
            PmsBrand brand = brandMap.get(spu.getBrandId());
            PmsCategory category = categoryMap.get(spu.getCategoryId());
            List<PmsSku> spuSkuList = skuGroupMap.getOrDefault(spu.getId(), Collections.emptyList());
            List<PmsSpuFullReduction> spuReductions = reductionGroupMap.getOrDefault(spu.getId(), Collections.emptyList());

            PmsSpuCacheDTO cacheDTO = buildSingleSpuCache(
                    spu, detail, brand, category, spuSkuList, stockMap, promotionMap, spuReductions, cacheTime
            );

            cacheDTOList.add(cacheDTO);
        }

        return cacheDTOList;
    }

    private PmsSpuCacheDTO buildSingleSpuCache(
            PmsSpu spu,
            PmsSpuDetail detail,
            PmsBrand brand,
            PmsCategory category,
            List<PmsSku> skuList,
            Map<Long, PmsSkuStock> stockMap,
            Map<Long, PmsSkuPromotion> promotionMap,
            List<PmsSpuFullReduction> reductionList,
            long cacheTime) {

        // 1. 构建SPU基础信息
        PmsSpuCacheDTO.SpuBasicInfo spuBasic = cacheConverter.toSpuBasicInfo(spu);

        // 2. 构建SPU详情信息
        PmsSpuCacheDTO.SpuDetailInfo spuDetail = detail != null
                ? cacheConverter.toSpuDetailInfo(detail, spu)
                : null;

        // 3. 构建品牌信息
        PmsSpuCacheDTO.BrandInfo brandInfo = brand != null
                ? cacheConverter.toBrandInfo(brand)
                : null;

        // 4. 构建分类信息
        PmsSpuCacheDTO.CategoryInfo categoryInfo = category != null
                ? cacheConverter.toCategoryInfo(category, spu.getCategoryIds())
                : null;

        // 5. 构建SKU列表
        List<PmsSpuCacheDTO.SkuInfo> skuInfoList = skuList.stream()
                .map(sku -> {
                    PmsSkuStock stock = stockMap.get(sku.getId());
                    PmsSkuPromotion promotion = promotionMap.get(sku.getId());

                    return PmsSpuCacheDTO.SkuInfo.builder()
                            .basic(cacheConverter.toSkuBasicInfo(sku))
                            .price(cacheConverter.toSkuPriceInfo(sku))
                            .promotion(promotion != null ? cacheConverter.toSkuPromotionInfo(promotion) : null)
                            .benefit(promotion != null ? cacheConverter.toSkuBenefitInfo(promotion) : null)
                            .config(stock != null ? cacheConverter.toSkuConfigInfo(stock) : null)
                            .build();
                })
                .collect(Collectors.toList());

        // 6. 构建满减规则列表
        List<PmsSpuCacheDTO.FullReductionInfo> fullReductionInfoList =
                cacheConverter.toFullReductionInfoList(reductionList);

        // 7. 组装聚合根
        return PmsSpuCacheDTO.builder()
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
