package com.mallease.product.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.cmd.SkuCmd;
import com.mallease.product.model.client.vo.SkuVO;
import com.mallease.product.model.data.entity.Sku;
import com.mallease.product.model.data.entity.SkuStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

/**
 * SKU转换器
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Mapper(componentModel = "spring")
public interface SkuConverter {

    // Entity → VO

    @Mapping(source = "attrValues", target = "attrValuesObj", qualifiedByName = "parseAttrValues")
    SkuVO entityToVo(Sku entity);

    List<SkuVO> entityListToVoList(List<Sku> entities);

    // 合并关联数据到 VO

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuId", ignore = true)
    void mergeSkuStockToVo(@MappingTarget SkuVO vo, SkuStock stock);

    // Cmd → Entity

    @Mapping(target = "deleted", constant = "0")
    @Mapping(source = "attrValues", target = "attrValues", qualifiedByName = "serializeAttrValues")
    Sku saveCmdToEntity(SkuCmd cmd);

    @Mapping(source = "attrValues", target = "attrValues", qualifiedByName = "serializeAttrValues")
    void updateEntityFromCmd(@MappingTarget Sku entity, SkuCmd cmd);

    @Mapping(target = "lockStock", constant = "0")
    @Mapping(target = "sale", constant = "0")
    @Mapping(target = "version", constant = "1")
    SkuStock stockCmdToEntity(SkuCmd.SkuStockCmd cmd);

    // Cmd → SkuData（聚合对象）

    /**
     * 将 SkuCmd 转换为 SpuAggregate.SkuData
     */
    default SpuAggregate.SkuData saveCmdToSkuData(SkuCmd cmd) {
        if (cmd == null) {
            return null;
        }
        return SpuAggregate.SkuData.builder()
                .sku(saveCmdToEntity(cmd))
                .stock(cmd.getStock() != null ? stockCmdToEntity(cmd.getStock()) : null)
                .build();
    }

    // 工具方法

    @Named("parseAttrValues")
    default List<SkuVO.AttrValueVO> parseAttrValues(String attrValues) {
        if (attrValues == null || attrValues.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> rawList = objectMapper.readValue(attrValues, new TypeReference<>() {});
            return rawList.stream()
                    .map(map -> SkuVO.AttrValueVO.builder()
                            .attrId(map.get("attrId") != null ? Long.valueOf(map.get("attrId").toString()) : null)
                            .attrName(map.get("attrName") != null ? map.get("attrName").toString() : null)
                            .attrValue(map.get("attrValue") != null ? map.get("attrValue").toString() : null)
                            .build())
                    .collect(java.util.stream.Collectors.toList());
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("serializeAttrValues")
    default String serializeAttrValues(List<SkuCmd.AttrValueCmd> attrValues) {
        if (attrValues == null || attrValues.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(attrValues);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
