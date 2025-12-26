package com.mallease.pms.converter;

import com.mallease.pms.dto.cmd.CreatePmsSkuCmd;
import com.mallease.pms.dto.cmd.CreatePmsSpuCmd;
import com.mallease.pms.dto.vo.PmsSkuVO;
import com.mallease.pms.dto.vo.PmsSpuDetailVO;
import com.mallease.pms.pojo.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * PMS转换器辅助类
 * <p>
 * 处理嵌套对象、关联数据的转换，辅助主转换器完成复杂对象映射
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Mapper(componentModel = "spring")
public interface PmsConverterHelper {

    // ========================================================================
    // SPU 相关嵌套对象转换
    // ========================================================================

    /**
     * SpuParamValueCmd → PmsSpuParamValue Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuId", ignore = true)  // 由 Service 层设置
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    PmsSpuParamValue spuParamValueCmdToEntity(CreatePmsSpuCmd.SpuParamValueCmd cmd);

    /**
     * PmsSpuParamValue Entity → SpuParamValueVO
     */
    @Mapping(target = "paramName", ignore = true)  // 由 Service 层填充
    PmsSpuDetailVO.SpuParamValueVO spuParamValueEntityToVo(PmsSpuParamValue entity);

    /**
     * 批量转换：Cmd List → Entity List
     */
    List<PmsSpuParamValue> spuParamValueCmdListToEntityList(List<CreatePmsSpuCmd.SpuParamValueCmd> cmdList);

    /**
     * 批量转换：Entity List → VO List
     */
    List<PmsSpuDetailVO.SpuParamValueVO> spuParamValueEntityListToVoList(List<PmsSpuParamValue> entityList);

    /**
     * SpuFullReductionCmd → PmsSpuFullReduction Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuId", ignore = true)  // 由 Service 层设置
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    PmsSpuFullReduction spuFullReductionCmdToEntity(CreatePmsSpuCmd.SpuFullReductionCmd cmd);

    /**
     * PmsSpuFullReduction Entity → SpuFullReductionVO
     */
    default PmsSpuDetailVO.SpuFullReductionVO spuFullReductionEntityToVo(PmsSpuFullReduction entity) {
        if (entity == null) {
            return null;
        }
        return PmsSpuDetailVO.SpuFullReductionVO.builder()
                .id(entity.getId())
                .fullPrice(entity.getFullPrice())
                .reducePrice(entity.getReducePrice())
                .description(formatFullReductionDesc(entity.getFullPrice(), entity.getReducePrice()))
                .build();
    }

    /**
     * 批量转换：Cmd List → Entity List
     */
    List<PmsSpuFullReduction> spuFullReductionCmdListToEntityList(List<CreatePmsSpuCmd.SpuFullReductionCmd> cmdList);

    /**
     * 批量转换：Entity List → VO List
     */
    List<PmsSpuDetailVO.SpuFullReductionVO> spuFullReductionEntityListToVoList(List<PmsSpuFullReduction> entityList);

    // ========================================================================
    // SKU 相关嵌套对象转换
    // ========================================================================

    /**
     * SkuMemberPriceCmd → PmsSkuMemberPrice Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "skuId", ignore = true)  // 由 Service 层设置
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    PmsSkuMemberPrice skuMemberPriceCmdToEntity(CreatePmsSkuCmd.SkuMemberPriceCmd cmd);

    /**
     * PmsSkuMemberPrice Entity → SkuMemberPriceVO
     */
    PmsSkuVO.SkuMemberPriceVO skuMemberPriceEntityToVo(PmsSkuMemberPrice entity);

    /**
     * 批量转换：Cmd List → Entity List
     */
    List<PmsSkuMemberPrice> skuMemberPriceCmdListToEntityList(List<CreatePmsSkuCmd.SkuMemberPriceCmd> cmdList);

    /**
     * 批量转换：Entity List → VO List
     */
    List<PmsSkuVO.SkuMemberPriceVO> skuMemberPriceEntityListToVoList(List<PmsSkuMemberPrice> entityList);

    /**
     * SkuLadderCmd → PmsSkuLadder Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "skuId", ignore = true)  // 由 Service 层设置
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    PmsSkuLadder skuLadderCmdToEntity(CreatePmsSkuCmd.SkuLadderCmd cmd);

    /**
     * PmsSkuLadder Entity → SkuLadderVO
     */
    default PmsSkuVO.SkuLadderVO skuLadderEntityToVo(PmsSkuLadder entity) {
        if (entity == null) {
            return null;
        }
        return PmsSkuVO.SkuLadderVO.builder()
                .id(entity.getId())
                .count(entity.getCount())
                .discount(entity.getDiscount())
                .price(entity.getPrice())
                .description(formatLadderDesc(entity.getCount(), entity.getDiscount()))
                .build();
    }

    /**
     * 批量转换：Cmd List → Entity List
     */
    List<PmsSkuLadder> skuLadderCmdListToEntityList(List<CreatePmsSkuCmd.SkuLadderCmd> cmdList);

    /**
     * 批量转换：Entity List → VO List
     */
    List<PmsSkuVO.SkuLadderVO> skuLadderEntityListToVoList(List<PmsSkuLadder> entityList);

    /**
     * SkuPromotionCmd → PmsSkuPromotion Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "skuId", ignore = true)  // 由 Service 层设置
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    PmsSkuPromotion skuPromotionCmdToEntity(CreatePmsSkuCmd.SkuPromotionCmd cmd);

    // ========================================================================
    // 工具方法
    // ========================================================================

    /**
     * 格式化满减描述
     */
    default String formatFullReductionDesc(BigDecimal fullPrice, BigDecimal reducePrice) {
        if (fullPrice == null || reducePrice == null) {
            return "";
        }
        return String.format("满 ¥%s 减 ¥%s", fullPrice, reducePrice);
    }

    /**
     * 格式化阶梯价描述
     */
    default String formatLadderDesc(Integer count, BigDecimal discount) {
        if (count == null || discount == null) {
            return "";
        }
        int discountPercent = discount.multiply(new BigDecimal("100")).intValue();
        return String.format("满 %d 件享 %d 折", count, discountPercent);
    }
}