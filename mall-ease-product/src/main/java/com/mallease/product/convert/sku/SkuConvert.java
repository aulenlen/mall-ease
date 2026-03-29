package com.mallease.product.convert.sku;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.product.controller.admin.sku.vo.SkuRespVO;
import com.mallease.product.controller.admin.sku.vo.SkuSaveReqVO;
import com.mallease.product.dal.entity.Sku;
import com.mallease.product.dal.entity.SkuStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Map;

/**
 * SKU 转换器
 */
@Mapper(componentModel = "spring")
public interface SkuConvert {

    @Mapping(target = "attrValues", ignore = true)
    @Mapping(target = "attrValuesObj", ignore = true)
    SkuRespVO toSkuResp(Sku entity);

    List<SkuRespVO> toSkuRespList(List<Sku> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuId", ignore = true)
    void mergeSkuStockToRespVO(@MappingTarget SkuRespVO respVO, SkuStock stock);

    @Mapping(target = "deleted", constant = "0")
    Sku toSku(SkuSaveReqVO reqVO);

    void copyToSku(@MappingTarget Sku entity, SkuSaveReqVO reqVO);

    @Mapping(target = "lockStock", constant = "0")
    @Mapping(target = "sale", constant = "0")
    @Mapping(target = "version", constant = "1")
    SkuStock stockReqVOToEntity(SkuSaveReqVO.SkuStockReqVO reqVO);

    @Named("parseAttrValues")
    default List<SkuRespVO.AttrValueRespVO> parseAttrValues(String attrValues) {
        if (attrValues == null || attrValues.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> rawList = objectMapper.readValue(attrValues, new TypeReference<>() {});
            return rawList.stream()
                    .map(map -> SkuRespVO.AttrValueRespVO.builder()
                            .attrId(map.get("attrId") != null ? Long.valueOf(map.get("attrId").toString()) : null)
                            .attrName(map.get("attrName") != null ? map.get("attrName").toString() : null)
                            .attrValue(map.get("attrValue") != null ? map.get("attrValue").toString() : null)
                            .build())
                    .toList();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    default SkuRespVO toSkuResp(Sku entity, String attrValues) {
        SkuRespVO respVO = toSkuResp(entity);
        respVO.setAttrValues(attrValues);
        respVO.setAttrValuesObj(parseAttrValues(attrValues));
        return respVO;
    }

    default List<SkuRespVO> toSkuRespList(List<Sku> entities, Map<Long, String> attrValuesMap) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(entity -> toSkuResp(entity, attrValuesMap.get(entity.getId())))
                .toList();
    }
}