package com.mallease.product.convert.stock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.product.controller.admin.stock.vo.SkuStockRespVO;
import com.mallease.product.controller.admin.stock.vo.SkuStockSaveReqVO;
import com.mallease.product.dal.entity.SkuStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

/**
 * SKU 库存转换器
 */
@Mapper(componentModel = "spring")
public interface SkuStockConvert {

    @Mapping(target = "lowStockWarning", source = ".", qualifiedByName = "calcLowStockWarning")
    @Mapping(target = "spuName", ignore = true)
    @Mapping(target = "attrValues", ignore = true)
    @Mapping(target = "attrValuesObj", ignore = true)
    SkuStockRespVO entityToRespVO(SkuStock entity);

    List<SkuStockRespVO> entityListToRespVOList(List<SkuStock> entities);

    SkuStock reqVOToEntity(SkuStockSaveReqVO reqVO);

    void updateEntityFromReqVO(@MappingTarget SkuStock entity, SkuStockSaveReqVO reqVO);

    @Named("calcLowStockWarning")
    default Boolean calcLowStockWarning(SkuStock stock) {
        if (stock == null || stock.getLowStock() == null || stock.getLowStock() <= 0) {
            return false;
        }
        return stock.getStock() != null && stock.getStock() <= stock.getLowStock();
    }

    @Named("parseAttrValues")
    default List<SkuStockRespVO.AttrValueVO> parseAttrValues(String attrValues) {
        if (attrValues == null || attrValues.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<java.util.Map<String, Object>> rawList = objectMapper.readValue(
                    attrValues,
                    new com.fasterxml.jackson.core.type.TypeReference<>() {
                    }
            );
            return rawList.stream()
                    .map(map -> SkuStockRespVO.AttrValueVO.builder()
                            .attrId(map.get("attrId") != null ? Long.valueOf(map.get("attrId").toString()) : null)
                            .attrName(map.get("attrName") != null ? map.get("attrName").toString() : null)
                            .attrValue(map.get("attrValue") != null ? map.get("attrValue").toString() : null)
                            .build())
                    .toList();
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}