package com.mallease.pms.assembler;

import com.mallease.pms.converter.PmsConverterHelper;
import com.mallease.pms.converter.PmsSkuConverter;
import com.mallease.pms.converter.PmsSpuConverter;
import com.mallease.pms.dto.context.SpuDetailData;
import com.mallease.pms.dto.vo.PmsSkuVO;
import com.mallease.pms.dto.vo.PmsSpuDetailVO;
import com.mallease.pms.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SPU详情数据组装器
 * 负责将 Service 层返回的聚合数据转换为前端所需的 VO 结构
 *
 * @author: Aulen
 * @create: 2025-12-21
 */
@Component
public class PmsSpuDetailAssembler {

    @Autowired
    private PmsSpuConverter spuConverter;

    @Autowired
    private PmsSkuConverter skuConverter;

    @Autowired
    private PmsConverterHelper converterHelper;

    /**
     * 组装 SPU 详情 VO
     *
     * @param data Service 层返回的聚合数据
     * @return 前端所需的完整 VO
     */
    public PmsSpuDetailVO assemble(SpuDetailData data) {
        // 1. 转换 SPU 基础信息
        PmsSpuDetailVO vo = spuConverter.entityToDetailVo(data.getSpu());

        // 2. 合并 SPU 详情
        if (data.getDetail() != null) {
            spuConverter.mergeSpuDetailToVo(vo, data.getDetail());
        }

        // 3. 组装 SKU 列表
        vo.setSkuList(assembleSkuList(data));

        // 4. 转换参数属性值
        vo.setAttributeValueList(
            converterHelper.spuAttributeValueEntityListToVoList(data.getAttributeValueList()));

        // 5. 转换满减规则
        vo.setFullReductionList(
            converterHelper.spuFullReductionEntityListToVoList(data.getFullReductionList()));

        // 6. 设置关联 ID
        vo.setSubjectIds(data.getSubjectIds());
        vo.setPreferenceAreaIds(data.getPreferenceAreaIds());

        return vo;
    }

    /**
     * 组装 SKU 列表（封装 Map 构建和循环拼装逻辑）
     */
    private List<PmsSkuVO> assembleSkuList(SpuDetailData data) {
        List<PmsSkuVO> skuVOList = skuConverter.entityListToVoList(data.getSkuList());
        if (skuVOList == null || skuVOList.isEmpty()) {
            return skuVOList;
        }

        // 构建 skuId -> 各类数据的映射
        Map<Long, PmsSkuStock> stockMap = data.getSkuStockList() != null
                ? data.getSkuStockList().stream()
                        .collect(Collectors.toMap(PmsSkuStock::getSkuId, s -> s, (a, b) -> a))
                : Map.of();

        Map<Long, PmsSkuPromotion> promotionMap = data.getSkuPromotionList() != null
                ? data.getSkuPromotionList().stream()
                        .collect(Collectors.toMap(PmsSkuPromotion::getSkuId, p -> p, (a, b) -> a))
                : Map.of();

        Map<Long, List<PmsSkuLadder>> ladderMap = data.getSkuLadderList() != null
                ? data.getSkuLadderList().stream()
                        .collect(Collectors.groupingBy(PmsSkuLadder::getSkuId))
                : Map.of();

        Map<Long, List<PmsSkuMemberPrice>> memberPriceMap = data.getSkuMemberPriceList() != null
                ? data.getSkuMemberPriceList().stream()
                        .collect(Collectors.groupingBy(PmsSkuMemberPrice::getSkuId))
                : Map.of();

        // 合并关联信息到 SKU VO
        skuVOList.forEach(skuVO -> {
            Long skuId = skuVO.getId();

            // 合并库存
            PmsSkuStock stock = stockMap.get(skuId);
            if (stock != null) {
                skuConverter.mergeSkuStockToVo(skuVO, stock);
            }

            // 合并促销
            PmsSkuPromotion promotion = promotionMap.get(skuId);
            if (promotion != null) {
                skuConverter.mergeSkuPromotionToVo(skuVO, promotion);
            }

            // 合并阶梯价
            List<PmsSkuLadder> ladders = ladderMap.get(skuId);
            if (ladders != null && !ladders.isEmpty()) {
                skuVO.setLadderList(skuConverter.ladderEntityListToVoList(ladders));
            }

            // 合并会员价
            List<PmsSkuMemberPrice> memberPrices = memberPriceMap.get(skuId);
            if (memberPrices != null && !memberPrices.isEmpty()) {
                skuVO.setMemberPriceList(skuConverter.memberPriceEntityListToVoList(memberPrices));
            }
        });

        return skuVOList;
    }
}
