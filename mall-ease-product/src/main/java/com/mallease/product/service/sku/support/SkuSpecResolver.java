package com.mallease.product.service.sku.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.mapper.AttributeValueDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkuSpecResolver {

    private final AttributeValueDao attributeValueDao;
    private final ObjectMapper objectMapper;

    public Map<Long, String> buildAttrValueJsonMap(List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, List<AttributeValue>> skuSpecMap = attributeValueDao.selectSpecsBySkuIds(skuIds).stream()
                .filter(item -> item.getSkuId() != null)
                .collect(Collectors.groupingBy(AttributeValue::getSkuId));

        Map<Long, String> result = new LinkedHashMap<>();
        for (Long skuId : skuIds.stream().filter(Objects::nonNull).distinct().toList()) {
            result.put(skuId, serializeAttrValues(skuSpecMap.get(skuId)));
        }
        return result;
    }

    private String serializeAttrValues(List<AttributeValue> specValues) {
        if (specValues == null || specValues.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(specValues.stream()
                    .map(item -> new SkuAttrValuePayload(item.getAttrId(), item.getAttrName(), item.getAttrValue()))
                    .toList());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("SKU规格序列化失败", e);
        }
    }

    private record SkuAttrValuePayload(Long attrId, String attrName, String attrValue) {
    }
}