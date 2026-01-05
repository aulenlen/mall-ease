package com.mallease.product.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.cmd.SkuCmd;
import com.mallease.product.model.client.vo.SkuVO;
import com.mallease.product.model.data.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

/**
 * SKU转换器
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Mapper(componentModel = "spring")
public interface SkuConverter {

    // Entity → VO

    @Mapping(source = "specValues", target = "specValuesObj", qualifiedByName = "parseSpecValues")
    SkuVO entityToVo(Sku entity);

    List<SkuVO> entityListToVoList(List<Sku> entities);

    // 合并关联数据到 VO

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuId", ignore = true)
    void mergeSkuStockToVo(@MappingTarget SkuVO vo, SkuStock stock);

    @Mapping(target = "id", ignore = true)
    void mergeSkuPromotionToVo(@MappingTarget SkuVO vo, SkuPromotion promotion);
    SkuVO.SkuLadderVO ladderEntityToVo(SkuLadder entity);
    List<SkuVO.SkuLadderVO> ladderEntityListToVoList(List<SkuLadder> entities);
    SkuVO.SkuMemberPriceVO memberPriceEntityToVo(SkuMemberPrice entity);
    List<SkuVO.SkuMemberPriceVO> memberPriceEntityListToVoList(List<SkuMemberPrice> entities);

    // Cmd → Entity

    @Mapping(target = "deleted", constant = "0")
    @Mapping(source = "specValues", target = "specValues", qualifiedByName = "serializeSpecValues")
    Sku saveCmdToEntity(SkuCmd cmd);

    @Mapping(source = "specValues", target = "specValues", qualifiedByName = "serializeSpecValues")
    void updateEntityFromCmd(@MappingTarget Sku entity, SkuCmd cmd);

    @Mapping(target = "lockStock", constant = "0")
    @Mapping(target = "sale", constant = "0")
    @Mapping(target = "version", constant = "1")
    SkuStock stockCmdToEntity(SkuCmd.SkuStockCmd cmd);

    @Mapping(target = "version", constant = "1")
    @Mapping(target = "previewStatus", constant = "0")
    SkuPromotion promotionCmdToEntity(SkuCmd.SkuPromotionCmd cmd);
    SkuMemberPrice memberPriceCmdToEntity(SkuCmd.SkuMemberPriceCmd cmd);
    List<SkuMemberPrice> memberPriceCmdListToEntityList(List<SkuCmd.SkuMemberPriceCmd> cmdList);
    SkuLadder ladderCmdToEntity(SkuCmd.SkuLadderCmd cmd);
    List<SkuLadder> ladderCmdListToEntityList(List<SkuCmd.SkuLadderCmd> cmdList);

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
                .promotion(cmd.getPromotion() != null ? promotionCmdToEntity(cmd.getPromotion()) : null)
                .ladderList(cmd.getLadderList() != null ? ladderCmdListToEntityList(cmd.getLadderList()) : null)
                .memberPriceList(cmd.getMemberPriceList() != null ? memberPriceCmdListToEntityList(cmd.getMemberPriceList()) : null)
                .build();
    }

    // 工具方法

    @Named("parseSpecValues")
    default List<SkuVO.SkuSpecValue> parseSpecValues(String specValues) {
        if (specValues == null || specValues.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(specValues, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("serializeSpecValues")
    default String serializeSpecValues(List<SkuCmd.SkuSpecValue> specValues) {
        if (specValues == null || specValues.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(specValues);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}