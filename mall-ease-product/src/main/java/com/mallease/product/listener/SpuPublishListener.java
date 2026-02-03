package com.mallease.product.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuIndexDTO;
import com.mallease.product.converter.SpuConverter;

import com.mallease.product.event.SpuPublishEvent;
import com.mallease.product.feign.SpuSearchFeignClient;
import com.mallease.product.model.data.entity.*;
import com.mallease.product.service.AttributeService;
import com.mallease.product.service.CategoryService;
import com.mallease.product.service.SkuService;
import com.mallease.product.service.SpuCacheService;
import com.mallease.product.service.SpuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SPU 上下架缓存监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpuPublishListener {

    private final SpuService spuService;
    private final SkuService skuService;
    private final ObjectMapper objectMapper;
    private final AttributeService attributeService;
    private final CategoryService categoryService;
    private final SpuConverter spuConverter;
    private final SpuCacheService spuCacheService;
    private final SpuSearchFeignClient spuSearchFeignClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(SpuPublishEvent event) {
        List<Long> spuIds = event.getSpuIds();
        try {
            if (event.getPublishStatus() == 1) {

                log.info("开始预热SPU缓存，商品数量: {}", spuIds.size());
                spuCacheService.warmUpBatch(spuIds);

                // 先尝试只更新状态
                R<List<Long>> publishResult = spuSearchFeignClient.publish(spuIds);

                if (publishResult != null && publishResult.getData() != null) {
                    List<Long> ids = publishResult.getData();
                    if (ids.isEmpty()) {
                        log.info("ES商品全部存在，仅更新状态，数量: {}", spuIds.size());
                    } else {
                        // 有缺失，执行返回的ids
                        log.info("ES中缺少部分SPU，执行全量索引，请求: {}, 不存在: {}", spuIds, ids);
                        List<SpuIndexDTO> spuIndexDTOList = buildIndex(ids);
                        spuSearchFeignClient.indexBatch(spuIndexDTOList);
                    }
                }
                log.info("SPU缓存预热完成");
            } else {
                // 下架：清除缓存
                spuCacheService.evictBatch(spuIds);

                spuSearchFeignClient.unpublish(spuIds);

                log.info("清除下架SPU缓存，数量: {}", spuIds);
            }
        } catch (Exception e) {
            log.error("缓存操作失败，spuIds: {}", spuIds, e);
        }
    }

    /**
     * 构建ElasticsearchDTO对象列表
     *
     * @param successIds 商品ID列表
     * @return ES索引DTO列表
     */
    private List<SpuIndexDTO> buildIndex(List<Long> successIds) {
        List<Spu> pmsSpuList = spuService.listByIds(successIds);
        if (pmsSpuList.isEmpty()) {
            return Collections.emptyList();

        }

        List<SpuIndexDTO> spuIndexDTOList = spuConverter.entityListToIndexDtoList(pmsSpuList);
        Map<Long, SpuIndexDTO> indexMap = spuIndexDTOList.stream().collect(Collectors.toMap(SpuIndexDTO::getSpuId, c -> c));

        List<Long> categoryIds = pmsSpuList.stream().map(Spu::getCategoryId).distinct().toList();
        List<Category> categoryList = categoryService.listByIds(categoryIds);
        Map<Long, String> categoryPathMap = categoryList.stream().collect(Collectors.toMap(Category::getId, Category::getPath));

        List<Sku> skuList = skuService.selectBySpuIds(successIds);
        Map<Long, List<Sku>> skuGroupMap = skuList.stream().collect(Collectors.groupingBy(Sku::getSpuId));

        List<AttributeValue> paramValueList = attributeService.listParamValuesBySpuIds(successIds);
        Map<Long, List<AttributeValue>> paramValueGroupMap = paramValueList.stream().collect(Collectors.groupingBy(AttributeValue::getSpuId));

        // 查询属性定义以获取 filterable、searchable 等信息
        Set<Long> attrIds = new HashSet<>();
        paramValueList.forEach(pv -> attrIds.add(pv.getAttrId()));
        skuList.forEach(sku -> {
            if (StringUtils.hasText(sku.getAttrValues())) {
                try {
                    List<Map<String, Object>> specList = objectMapper.readValue(sku.getAttrValues(), new TypeReference<>() {});
                    if (specList != null) {
                        specList.forEach(spec -> {
                            if (spec.get("attrId") != null) {
                                attrIds.add(((Number) spec.get("attrId")).longValue());
                            }
                        });
                    }
                } catch (JsonProcessingException ignored) {}
            }
        });

        Map<Long, Attribute> attrMap = Map.of();
        if (!attrIds.isEmpty()) {
            List<Attribute> attrList = attributeService.listByIds(attrIds.stream().toList());
            attrMap = attrList.stream().collect(Collectors.toMap(Attribute::getId, a -> a));
        }

        Map<Long, Attribute> finalAttrMap = attrMap;
        for (Spu spu : pmsSpuList) {
            SpuIndexDTO dto = indexMap.get(spu.getId());
            dto.setInStock(spu.getStock() != null && spu.getStock() > 0);
            dto.setCategoryPath(categoryPathMap.get(spu.getCategoryId()));

            List<Sku> spuSkuList = skuGroupMap.getOrDefault(spu.getId(), Collections.emptyList());
            List<SpuIndexDTO.Sku> dtoSkuList = spuSkuList.stream().map(
                    sku -> SpuIndexDTO.Sku.builder()
                            .skuId(sku.getId())
                            .skuCode(sku.getSkuCode())
                            .price(sku.getPrice())
                            .build()
            ).toList();
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

                        // 获取属性定义信息
                        Attribute attr = attrId != null ? finalAttrMap.get(attrId) : null;
                        allAttrValues.add(SpuIndexDTO.AttrValue.builder()
                                .attrId(attrId)
                                .attrName(attrName)
                                .attrValue(attrValue)
                                .type(1) // 规格
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
}
