package com.mallease.product.assembler;

import com.mallease.product.converter.SkuConverter;
import com.mallease.product.converter.SpuConverter;
import com.mallease.product.model.aggregate.SpuAggregate;
import com.mallease.product.model.client.vo.SkuVO;
import com.mallease.product.model.client.vo.SpuDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SPU详情数据组装器
 * 负责将 Service 层返回的聚合数据转换为前端所需的 VO 结构
 *
 * @author: Aulen
 * @create: 2025-12-21
 */
@Component
public class SpuDetailAssembler {

    @Autowired
    private SpuConverter spuConverter;

    @Autowired
    private SkuConverter skuConverter;

    /**
     * 组装 SPU 详情 VO
     *
     * @param aggregate Service 层返回的聚合数据
     * @return 前端所需的完整 VO
     */
    public SpuDetailVO assemble(SpuAggregate aggregate) {
        // 1. 转换 SPU 基础信息
        SpuDetailVO vo = spuConverter.entityToDetailVo(aggregate.getSpu());

        // 2. 合并 SPU 详情
        if (aggregate.getSpuDetail() != null) {
            spuConverter.mergeSpuDetailToVo(vo, aggregate.getSpuDetail());
        }

        // 3. 组装 SKU 列表
        vo.setSkuList(assembleSkuList(aggregate.getSkuList()));

        // 4. 转换属性值
        vo.setAttrValueList(
                spuConverter.attrValueEntityListToVoList(aggregate.getAttrValueList()));

        // 5. 转换满减规则
        vo.setFullReductionList(
                spuConverter.fullReductionEntityListToVoList(aggregate.getFullReductionList()));

        // 6. 设置关联 ID
        vo.setSubjectIds(aggregate.getSubjectIds());
        vo.setPreferenceAreaIds(aggregate.getPreferenceAreaIds());
        return vo;
    }

    /**
     * 组装 SKU 列表（从嵌套的 SkuData 结构转换为 VO）
     */
    private List<SkuVO> assembleSkuList(List<SpuAggregate.SkuData> skuDataList) {
        if (skuDataList == null || skuDataList.isEmpty()) {
            return List.of();
        }

        return skuDataList.stream()
                .map(this::assembleSkuVO)
                .collect(Collectors.toList());
    }

    /**
     * 组装单个 SKU VO
     */
    private SkuVO assembleSkuVO(SpuAggregate.SkuData skuData) {
        // 转换 SKU 基础信息
        SkuVO skuVO = skuConverter.entityToVo(skuData.getSku());

        // 合并库存
        if (skuData.getStock() != null) {
            skuConverter.mergeSkuStockToVo(skuVO, skuData.getStock());
        }

        // 合并促销
        if (skuData.getPromotion() != null) {
            skuConverter.mergeSkuPromotionToVo(skuVO, skuData.getPromotion());
        }

        // 合并阶梯价
        if (skuData.getLadderList() != null && !skuData.getLadderList().isEmpty()) {
            skuVO.setLadderList(skuConverter.ladderEntityListToVoList(skuData.getLadderList()));
        }

        // 合并会员价
        if (skuData.getMemberPriceList() != null && !skuData.getMemberPriceList().isEmpty()) {
            skuVO.setMemberPriceList(skuConverter.memberPriceEntityListToVoList(skuData.getMemberPriceList()));
        }

        return skuVO;
    }
}
