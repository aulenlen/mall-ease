package com.mallease.pms.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSkuCmd;
import com.mallease.pms.dto.vo.PmsSkuVO;
import com.mallease.pms.pojo.*;
import jakarta.validation.Valid;
import org.mapstruct.*;

import java.util.List;

/**
 * SKU转换器
 * 负责SKU实体与DTO之间的转换，整合库存、促销等复合数据
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PmsSkuConverter {

    // ========================================================================
    // Entity → VO
    // ========================================================================

    /**
     * Entity → VO（基础映射）
     * 库存、促销等字段由 Service 层通过 merge 方法填充
     */
    @Mapping(source = "specValues", target = "specValuesObj", qualifiedByName = "parseSpecValues")
    PmsSkuVO entityToVo(PmsSku entity);

    /**
     * 列表转换
     */
    List<PmsSkuVO> entityListToVoList(List<PmsSku> entities);

    /**
     * 合并库存信息到 VO
     */
    void mergeSkuStockToVo(@MappingTarget PmsSkuVO vo, PmsSkuStock stock);

    /**
     * 合并促销信息到 VO
     */
    void mergeSkuPromotionToVo(@MappingTarget PmsSkuVO vo, PmsSkuPromotion promotion);

    // ========================================================================
    // Entity → VO（关联数据）
    // ========================================================================

    /**
     * 阶梯价 Entity → VO
     */
    PmsSkuVO.SkuLadderVO ladderEntityToVo(PmsSkuLadder entity);

    /**
     * 阶梯价 EntityList → VOList
     */
    List<PmsSkuVO.SkuLadderVO> ladderEntityListToVoList(List<PmsSkuLadder> entities);

    /**
     * 会员价 Entity → VO
     */
    PmsSkuVO.SkuMemberPriceVO memberPriceEntityToVo(PmsSkuMemberPrice entity);

    /**
     * 会员价 EntityList → VOList
     */
    List<PmsSkuVO.SkuMemberPriceVO> memberPriceEntityListToVoList(List<PmsSkuMemberPrice> entities);

    // ========================================================================
    // Command → Entity
    // ========================================================================

    /**
     * CreateCmd → PmsSku Entity
     */
    @Mapping(target = "deleted", constant = "0")
    PmsSku createCmdToEntity(CreatePmsSkuCmd cmd);

    /**
     * CreateCmd → PmsSku Entity
     */
    @Mapping(target = "deleted", constant = "0")
    List<PmsSku> createCmdListToEntityList(List<CreatePmsSkuCmd> cmdList);

    /**
     * UpdateCmd → Entity（部分更新）
     */
    void updateEntityFromCmd(@MappingTarget PmsSku entity, UpdatePmsSkuCmd cmd);

    // ========================================================================
    // SKU 关联数据转换（skuId 由 Service 层设置）
    // ========================================================================

    /**
     * 库存 Cmd → Entity
     */
    @Mapping(target = "lockStock", constant = "0")
    @Mapping(target = "sale", constant = "0")
    @Mapping(target = "version", constant = "1")
    PmsSkuStock stockCmdToEntity(CreatePmsSkuCmd.SkuStockCmd cmd);

    /**
     * 促销 Cmd → Entity
     */
    @Mapping(target = "version", constant = "1")
    @Mapping(target = "previewStatus", constant = "0")
    PmsSkuPromotion promotionCmdToEntity(CreatePmsSkuCmd.SkuPromotionCmd cmd);

    /**
     * 会员价 Cmd → Entity
     */
    PmsSkuMemberPrice memberPriceCmdToEntity(CreatePmsSkuCmd.SkuMemberPriceCmd cmd);
    /**
     * 会员价 CmdList → EntityList
     */
    List<PmsSkuMemberPrice> memberPriceCmdListToEntityList(List<CreatePmsSkuCmd.SkuMemberPriceCmd> cmdList);

    /**
     * 阶梯价 Cmd → Entity
     */
    PmsSkuLadder ladderCmdToEntity(CreatePmsSkuCmd.SkuLadderCmd cmd);

    /**
     * 阶梯价 CmdList → EntityList
     */
    List<PmsSkuLadder> ladderCmdListToEntityList(List<CreatePmsSkuCmd.SkuLadderCmd> cmdList);

    // ========================================================================
    // 自定义映射方法
    // ========================================================================

    /**
     * 解析 JSON 格式的规格值
     */
    @Named("parseSpecValues")
    default Object parseSpecValues(String specValues) {
        if (specValues == null || specValues.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(specValues, Object.class);
        } catch (JsonProcessingException e) {
            return specValues;
        }
    }
}
