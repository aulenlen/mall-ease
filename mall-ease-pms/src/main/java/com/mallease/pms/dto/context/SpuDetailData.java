package com.mallease.pms.dto.context;

import com.mallease.pms.pojo.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SPU详情聚合数据（Service层返回）
 * @author: Aulen
 * @create: 2025-12-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuDetailData {

    /**
     * SPU基础信息
     */
    private PmsSpu spu;

    /**
     * SPU详情信息
     */
    private PmsSpuDetail detail;

    /**
     * SKU列表
     */
    private List<PmsSku> skuList;

    /**
     * SKU库存列表
     */
    private List<PmsSkuStock> skuStockList;

    /**
     * SKU促销信息列表
     */
    private List<PmsSkuPromotion> skuPromotionList;

    /**
     * SKU阶梯价列表
     */
    private List<PmsSkuLadder> skuLadderList;

    /**
     * SKU会员价列表
     */
    private List<PmsSkuMemberPrice> skuMemberPriceList;

    /**
     * 参数属性值列表
     */
    private List<PmsSpuParamValue> paramValueList;

    /**
     * 满减规则列表
     */
    private List<PmsSpuFullReduction> fullReductionList;

    /**
     * 关联的专题ID列表
     */
    private List<Long> subjectIds;

    /**
     * 关联的优选专区ID列表
     */
    private List<Long> preferenceAreaIds;
}