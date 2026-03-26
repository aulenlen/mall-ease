package com.mallease.product.service.spu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuIndexDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.common.service.TypedRedisService;
import com.mallease.product.constant.ProductCacheKeys;
import com.mallease.product.convert.spu.SpuConvert;
import com.mallease.product.dal.entity.Attribute;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.entity.Category;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.Spu;
import com.mallease.product.dal.entity.SpuSnapshot;
import com.mallease.product.dal.mapper.SpuDao;
import com.mallease.product.dal.mapper.SpuSnapshotDao;
import com.mallease.product.feign.search.SpuSearchFeignClient;
import com.mallease.product.service.attribute.AttributeService;
import com.mallease.product.service.category.CategoryService;
import com.mallease.product.service.sku.SkuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SpuPublishServiceImpl implements SpuPublishService {

    private final SpuSnapshotService snapshotService;
    private final SpuDao spuDao;
    private final SpuSnapshotDao spuSnapshotDao;
    private final SpuService spuService;
    private final SkuService skuService;
    private final ObjectMapper objectMapper;
    private final AttributeService attributeService;
    private final CategoryService categoryService;
    private final SpuConvert spuConvert;
    private final TypedRedisService typedRedisService;
    private final SpuSearchFeignClient spuSearchFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publish(List<Long> spuIds) {
        List<Long> normalizedSpuIds = normalizeSpuIds(spuIds);
        LocalDateTime publishedAt = LocalDateTime.now();

        List<PublishSnapshotPlan> publishPlans = snapshotService.buildPublishPlans(normalizedSpuIds, publishedAt);
        snapshotService.saveSnapshots(publishPlans.stream().map(PublishSnapshotPlan::snapshot).toList());

        int updatedCount = spuDao.updatePublishStatusBatch(normalizedSpuIds, 1, publishedAt);
        runAfterCommit(() -> handlePublishSideEffects(publishPlans));

        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unpublish(List<Long> spuIds) {
        List<Long> normalizedSpuIds = normalizeSpuIds(spuIds);
        List<Spu> spus = spuDao.selectByIds(normalizedSpuIds);
        Map<Long, Spu> spuMap = spus.stream()
                .collect(Collectors.toMap(Spu::getId, Function.identity()));
        List<Long> missingSpuIds = normalizedSpuIds.stream()
                .filter(spuId -> !spuMap.containsKey(spuId))
                .toList();
        if (!missingSpuIds.isEmpty()) {
            throw new ApiException("商品不存在: " + missingSpuIds);
        }
        List<Long> unpublishedSpuIds = normalizedSpuIds.stream()
                .filter(spuId -> !Integer.valueOf(0).equals(spuMap.get(spuId).getPublishStatus()))
                .toList();
        if (!unpublishedSpuIds.isEmpty()) {
            int updatedCount = spuDao.updatePublishStatusBatch(unpublishedSpuIds, 0, null);
            spuSnapshotDao.updatePublishStatusBatch(unpublishedSpuIds, 0);
            runAfterCommit(() -> handleUnpublishSideEffects(unpublishedSpuIds));
            return updatedCount;
        }
        return 0;
    }

    private void handlePublishSideEffects(List<PublishSnapshotPlan> publishPlans) {
        if (publishPlans == null || publishPlans.isEmpty()) {
            return;
        }
        List<Long> spuIds = publishPlans.stream().map(PublishSnapshotPlan::spuId).toList();
        try {
            warmUpPublishedSnapshotCache(spuIds);
            syncPublishedSearch(publishPlans);
        } catch (Exception e) {
            log.error("发布后缓存或搜索同步失败，spuIds={}", spuIds, e);
        }
    }

    private void handleUnpublishSideEffects(List<Long> spuIds) {
        try {
            evictPublishedSnapshotCache(spuIds);
            spuSearchFeignClient.unpublish(spuIds);
        } catch (Exception e) {
            log.error("下架后缓存或搜索同步失败，spuIds={}", spuIds, e);
        }
    }

    private void warmUpPublishedSnapshotCache(List<Long> spuIds) {
        List<SpuSnapshot> snapshots = spuSnapshotDao.selectPublishedCacheBySpuIds(spuIds);
        if (snapshots == null || snapshots.isEmpty()) {
            log.info("未查询到可预热的已发布快照，spuIds: {}", spuIds);
            return;
        }

        Map<String, String> cacheMap = snapshots.stream()
                .filter(snapshot -> snapshot.getSpuId() != null)
                .filter(snapshot -> StringUtils.hasText(snapshot.getSnapshotJson()))
                .collect(Collectors.toMap(
                        snapshot -> ProductCacheKeys.spuDetailKey(snapshot.getSpuId()),
                        SpuSnapshot::getSnapshotJson,
                        (left, right) -> right,
                        LinkedHashMap::new
                ));

        if (cacheMap.isEmpty()) {
            log.info("已发布快照缺少可缓存的 JSON，spuIds: {}", spuIds);
            return;
        }

        typedRedisService.multiSetStringWithExpire(cacheMap, ProductCacheKeys.spuDetailTtlSeconds());
    }

    private void evictPublishedSnapshotCache(List<Long> spuIds) {
        List<String> keys = spuIds.stream()
                .map(ProductCacheKeys::spuDetailKey)
                .toList();
        typedRedisService.deleteBatch(keys);
    }

    private void syncPublishedSearch(List<PublishSnapshotPlan> publishPlans) {
        List<Long> changedSpuIds = publishPlans.stream()
                .filter(PublishSnapshotPlan::contentChanged)
                .map(PublishSnapshotPlan::spuId)
                .toList();
        List<Long> unchangedSpuIds = publishPlans.stream()
                .filter(plan -> !plan.contentChanged())
                .map(PublishSnapshotPlan::spuId)
                .toList();

        if (!changedSpuIds.isEmpty()) {
            log.info("检测到已发布内容变化，执行 ES 全量重建，请求: {}", changedSpuIds);
            List<SpuIndexDTO> changedIndexList = buildIndex(changedSpuIds);
            if (!changedIndexList.isEmpty()) {
                spuSearchFeignClient.indexBatch(changedIndexList);
            }
        }

        if (unchangedSpuIds.isEmpty()) {
            return;
        }

        R<List<Long>> publishResult = spuSearchFeignClient.publish(unchangedSpuIds);
        if (publishResult == null || publishResult.getData() == null) {
            return;
        }

        List<Long> missingIds = publishResult.getData();
        if (missingIds.isEmpty()) {
            log.info("ES商品内容未变化，仅更新上架状态，数量: {}", unchangedSpuIds.size());
            return;
        }

        log.info("ES中缺少部分未变化的SPU，执行补建索引，请求: {}, 不存在: {}", unchangedSpuIds, missingIds);
        List<SpuIndexDTO> missingIndexList = buildIndex(missingIds);
        if (!missingIndexList.isEmpty()) {
            spuSearchFeignClient.indexBatch(missingIndexList);
        }
    }

    private List<SpuIndexDTO> buildIndex(List<Long> successIds) {
        List<Spu> pmsSpuList = spuService.listByIds(successIds);
        if (pmsSpuList.isEmpty()) {
            return Collections.emptyList();
        }

        List<SpuIndexDTO> spuIndexDTOList = spuConvert.toIndexDtoList(pmsSpuList);
        Map<Long, SpuIndexDTO> indexMap = spuIndexDTOList.stream()
                .collect(Collectors.toMap(SpuIndexDTO::getSpuId, Function.identity()));

        List<Long> categoryIds = pmsSpuList.stream().map(Spu::getCategoryId).distinct().toList();
        List<Category> categoryList = categoryService.listByIds(categoryIds);
        Map<Long, String> categoryPathMap = categoryList.stream()
                .collect(Collectors.toMap(Category::getId, Category::getPath));

        List<Sku> skuList = skuService.selectEnabledBySpuIds(successIds);
        Map<Long, List<Sku>> skuGroupMap = skuList.stream().collect(Collectors.groupingBy(Sku::getSpuId));

        List<AttributeValue> paramValueList = attributeService.listParamValuesBySpuIds(successIds);
        Map<Long, List<AttributeValue>> paramValueGroupMap = paramValueList.stream()
                .collect(Collectors.groupingBy(AttributeValue::getSpuId));

        Set<Long> attrIds = new HashSet<>();
        paramValueList.forEach(pv -> attrIds.add(pv.getAttrId()));
        skuList.forEach(sku -> {
            if (!StringUtils.hasText(sku.getAttrValues())) {
                return;
            }
            try {
                List<Map<String, Object>> specList = objectMapper.readValue(sku.getAttrValues(), new TypeReference<>() {
                });
                if (specList != null) {
                    specList.forEach(spec -> {
                        if (spec.get("attrId") != null) {
                            attrIds.add(((Number) spec.get("attrId")).longValue());
                        }
                    });
                }
            } catch (JsonProcessingException e) {
                log.warn("解析SKU规格失败，skuId={}", sku.getId(), e);
            }
        });

        Map<Long, Attribute> attrMap = Map.of();
        if (!attrIds.isEmpty()) {
            List<Attribute> attrList = attributeService.listByIds(attrIds.stream().toList());
            attrMap = attrList.stream().collect(Collectors.toMap(Attribute::getId, Function.identity()));
        }

        Map<Long, Attribute> finalAttrMap = attrMap;
        for (Spu spu : pmsSpuList) {
            SpuIndexDTO dto = indexMap.get(spu.getId());
            dto.setInStock(Boolean.TRUE.equals(spu.getInStock()));
            dto.setCategoryPath(categoryPathMap.get(spu.getCategoryId()));

            List<Sku> spuSkuList = skuGroupMap.getOrDefault(spu.getId(), Collections.emptyList());
            List<SpuIndexDTO.Sku> dtoSkuList = spuSkuList.stream()
                    .map(sku -> SpuIndexDTO.Sku.builder()
                            .skuId(sku.getId())
                            .skuCode(sku.getSkuCode())
                            .basePrice(sku.getBasePrice())
                            .build())
                    .toList();
            dto.setSkuList(dtoSkuList);

            List<SpuIndexDTO.AttrValue> allAttrValues = new ArrayList<>();
            Set<String> seenSpecs = new HashSet<>();
            for (Sku sku : spuSkuList) {
                String jsonString = sku.getAttrValues();
                if (!StringUtils.hasText(jsonString)) {
                    continue;
                }

                try {
                    List<Map<String, Object>> specList = objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
                    if (specList == null) {
                        continue;
                    }

                    for (Map<String, Object> spec : specList) {
                        Long attrId = spec.get("attrId") != null ? ((Number) spec.get("attrId")).longValue() : null;
                        String attrName = (String) spec.get("attrName");
                        String attrValue = (String) spec.get("attrValue");
                        String uniqueKey = (attrId != null ? attrId : attrName) + ":" + attrValue;
                        if (seenSpecs.contains(uniqueKey)) {
                            continue;
                        }
                        seenSpecs.add(uniqueKey);

                        Attribute attr = attrId != null ? finalAttrMap.get(attrId) : null;
                        allAttrValues.add(SpuIndexDTO.AttrValue.builder()
                                .attrId(attrId)
                                .attrName(attrName)
                                .attrValue(attrValue)
                                .type(1)
                                .filterable(attr != null && Integer.valueOf(1).equals(attr.getFilterable()))
                                .searchable(attr != null && Integer.valueOf(1).equals(attr.getSearchable()))
                                .build());
                    }
                } catch (JsonProcessingException e) {
                    log.warn("解析 SKU 属性值失败，skuId={}, json={}", sku.getId(), jsonString, e);
                }
            }

            List<AttributeValue> spuParamValues = paramValueGroupMap.getOrDefault(spu.getId(), Collections.emptyList());
            for (AttributeValue pv : spuParamValues) {
                Attribute attr = finalAttrMap.get(pv.getAttrId());
                allAttrValues.add(SpuIndexDTO.AttrValue.builder()
                        .attrId(pv.getAttrId())
                        .attrName(pv.getAttrName())
                        .attrValue(pv.getAttrValue())
                        .type(0)
                        .filterable(attr != null && Integer.valueOf(1).equals(attr.getFilterable()))
                        .searchable(attr != null && Integer.valueOf(1).equals(attr.getSearchable()))
                        .build());
            }

            dto.setAttrValueList(allAttrValues);
        }

        return spuIndexDTOList;
    }

    private void runAfterCommit(Runnable runnable) {
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }
            });
            return;
        }
        runnable.run();
    }

    private List<Long> normalizeSpuIds(List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            throw new ApiException("商品ID不能为空");
        }
        List<Long> normalizedSpuIds = spuIds.stream()
                .distinct()
                .toList();
        if (normalizedSpuIds.stream().anyMatch(Objects::isNull)) {
            throw new ApiException("商品ID不能为空");
        }
        return normalizedSpuIds;
    }
}
