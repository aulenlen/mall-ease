package com.mallease.product.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.product.model.client.cmd.SkuStockCmd;
import com.mallease.product.model.client.vo.SkuStockVO;
import com.mallease.product.model.data.entity.SkuStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

/**
 * SKU库存转换器
 *
 * @author: Aulen
 * @create: 2026-01-05
 */
@Mapper(componentModel = "spring")
public interface SkuStockConverter {

    // Entity → VO

    @Mapping(target = "lowStockWarning", source = ".", qualifiedByName = "calcLowStockWarning")
    @Mapping(target = "spuName", ignore = true)
    @Mapping(target = "attrValues", ignore = true)
    @Mapping(target = "attrValuesObj", ignore = true)
    SkuStockVO entityToVo(SkuStock entity);

    List<SkuStockVO> entityListToVoList(List<SkuStock> entities);

    // Cmd → Entity

    @Mapping(target = "lockStock", constant = "0")
    @Mapping(target = "sale", constant = "0")
    @Mapping(target = "version", constant = "0")
    SkuStock saveCmdToEntity(SkuStockCmd cmd);

    void updateEntityFromCmd(@MappingTarget SkuStock entity, SkuStockCmd cmd);

    // 工具方法

    @Named("calcLowStockWarning")
    default Boolean calcLowStockWarning(SkuStock stock) {
        if (stock == null || stock.getLowStock() == null || stock.getLowStock() <= 0) {
            return false;
        }
        return stock.getStock() != null && stock.getStock() <= stock.getLowStock();
    }

    /**
     * 解析属性值 JSON 字符串为对象列表
     */
    @Named("parseAttrValues")
    default List<SkuStockVO.AttrValueVO> parseAttrValues(String attrValues) {
        if (attrValues == null || attrValues.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<java.util.Map<String, Object>> rawList = objectMapper.readValue(attrValues, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            return rawList.stream()
                    .map(map -> SkuStockVO.AttrValueVO.builder()
                            .attrId(map.get("attrId") != null ? Long.valueOf(map.get("attrId").toString()) : null)
                            .attrName(map.get("attrName") != null ? map.get("attrName").toString() : null)
                            .attrValue(map.get("attrValue") != null ? map.get("attrValue").toString() : null)
                            .build())
                    .collect(java.util.stream.Collectors.toList());
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}