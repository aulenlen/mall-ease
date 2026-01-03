package com.mallease.product.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.common.dto.remote.SpuIndexDTO;
import com.mallease.product.converter.SpuConverter;
import com.mallease.product.dao.CategoryDao;

import com.mallease.product.dao.ParamDao;
import com.mallease.product.dao.SpuDao;
import com.mallease.product.dao.SpuParamValueDao;
import com.mallease.product.event.SpuPublishEvent;
import com.mallease.product.feign.SpuSearchFeignClient;
import com.mallease.product.model.data.entity.*;
import com.mallease.product.service.SkuService;
import com.mallease.product.service.SpuCacheService;
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
public class SpuPublishListener {
    @Autowired
    private SpuDao spuDao;
    @Autowired
    private SkuService skuService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SpuParamValueDao spuParamValueDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ParamDao paramDao;
    @Autowired
    private SpuConverter spuConverter;
    @Autowired
    private SpuCacheService spuCacheService;
    @Autowired
    private SpuSearchFeignClient spuSearchFeignClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(SpuPublishEvent event) {

        // 处理缓存（缓存失败不影响业务结果）
        try {
            if (event.getPublishStatus() == 1) {
                // 上架：预热缓存
                log.info("开始预热SPU缓存，商品数量: {}", event.getSpuIds().size());
                spuCacheService.warmUpBatch(event.getSpuIds());
                List<SpuIndexDTO> spuIndexDTOList = buildIndex(event.getSpuIds());
                spuSearchFeignClient.indexBatch(spuIndexDTOList);
                log.info("SPU缓存预热完成");
            } else {
                // 下架：清除缓存
                spuCacheService.evictBatch(event.getSpuIds());
                log.info("清除下架SPU缓存，数量: {}", event.getSpuIds().size());
            }
        } catch (Exception e) {
            log.error("缓存操作失败，spuIds: {}", event.getSpuIds(), e);
        }
    }

    /**
     * 构建ElasticsearchDTO对象列表
     *
     * @param successIds 商品ID列表
     * @return ES索引DTO列表
     */
    private List<SpuIndexDTO> buildIndex(List<Long> successIds) {
        List<Spu> pmsSpuList = spuDao.selectByIds(successIds);
        if (pmsSpuList.isEmpty()) {
            return Collections.emptyList();

        }

        List<SpuIndexDTO> spuIndexDTOList = spuConverter.entityListToIndexDtoList(pmsSpuList);
        Map<Long, SpuIndexDTO> indexMap = spuIndexDTOList.stream().collect(Collectors.toMap(SpuIndexDTO::getSpuId, c -> c));

        // 查询分类路径
        List<Long> categoryIds = pmsSpuList.stream().map(Spu::getCategoryId).distinct().toList();
        List<Category> categoryList = categoryDao.selectByIds(categoryIds);
        Map<Long, String> categoryPathMap = categoryList.stream().collect(Collectors.toMap(Category::getId, Category::getPath));

        // 查询 SKU 列表
        List<Sku> skuList = skuService.selectBySpuIds(successIds);
        Map<Long, List<Sku>> skuGroupMap = skuList.stream().collect(Collectors.groupingBy(Sku::getSpuId));

        // 查询参数值并关联参数名称
        List<SpuParamValue> paramValueList = spuParamValueDao.selectBySpuIds(successIds);
        Map<Long, List<SpuParamValue>> paramValueGroupMap = paramValueList.stream().collect(Collectors.groupingBy(SpuParamValue::getSpuId));
        Set<Long> paramIds = paramValueList.stream().map(SpuParamValue::getParamId).collect(Collectors.toSet());
        Map<Long, String> paramNameMap = Map.of();
        if (!paramIds.isEmpty()) {
            List<Param> paramList = paramDao.selectByIds(paramIds.stream().toList());
            paramNameMap = paramList.stream().collect(Collectors.toMap(Param::getId, Param::getName));

        }

        Map<Long, String> finalParamNameMap = paramNameMap;
        for (Spu spu : pmsSpuList) {
            SpuIndexDTO dto = indexMap.get(spu.getId());
            dto.setInStock(spu.getStock() != null && spu.getStock() > 0);
            dto.setCategoryPath(categoryPathMap.get(spu.getCategoryId()));

            // 构建 SKU 列表
            List<Sku> spuSkuList = skuGroupMap.getOrDefault(spu.getId(), Collections.emptyList());
            List<SpuIndexDTO.Sku> dtoSkuList = spuSkuList.stream().map(
                    sku -> SpuIndexDTO.Sku.builder()
                            .skuId(sku.getId())
                            .skuCode(sku.getSkuCode())
                            .price(sku.getPrice())
                            .build()
            ).toList();
            dto.setSkuList(dtoSkuList);

            // 构建规格值列表（从 SKU 的 specValues JSON 解析）
            Set<String> seenSpecs = new HashSet<>();
            List<SpuIndexDTO.SpecValue> allSpecValues = new ArrayList<>();
            for (Sku sku : spuSkuList) {
                String jsonString = sku.getSpecValues();
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
                        Long specId = spec.get("specId") != null ? ((Number) spec.get("specId")).longValue() : null;
                        String specName = (String) spec.get("specName");
                        String specValue = (String) spec.get("value");
                        String uniqueKey = (specId != null ? specId : specName) + ":" + specValue;
                        if (seenSpecs.contains(uniqueKey)) {
                            continue;
                        }
                        seenSpecs.add(uniqueKey);
                        allSpecValues.add(SpuIndexDTO.SpecValue.builder()
                                .specId(specId)
                                .specName(specName)
                                .specValue(specValue)
                                .build());
                    }
                } catch (JsonProcessingException e) {
                    log.warn("解析 SKU 规格值失败，skuId={}, json={}", sku.getId(), jsonString, e);
                }
            }

            dto.setSpecValueList(allSpecValues);

            // 构建参数值列表
            List<SpuParamValue> spuParamValues = paramValueGroupMap.getOrDefault(spu.getId(), Collections.emptyList());
            List<SpuIndexDTO.ParamValue> paramValuesDTO = spuParamValues.stream()
                    .map(pv -> SpuIndexDTO.ParamValue.builder()
                            .paramId(pv.getParamId())
                            .paramName(finalParamNameMap.get(pv.getParamId()))
                            .paramValue(pv.getValue())
                            .build())
                    .toList();
            dto.setParamValueList(paramValuesDTO);
        }

        return spuIndexDTOList;
    }
}
