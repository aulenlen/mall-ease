package com.mallease.product.model.aggregate;

import com.mallease.product.model.data.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SPU聚合对象
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpuAggregate {

    // SPU 核心数据

    /**
     * SPU主表
     */
    private Spu spu;

    /**
     * SPU详情（可选）
     */
    private SpuDetail spuDetail;

    /**
     * SKU聚合数据列表
     */
    private List<SkuData> skuList;

    /**
     * SPU属性值列表（参数）
     */
    private List<AttributeValue> attrValueList;

    /**
     * 满减规则列表（可选）
     */
    private List<SpuFullReduction> fullReductionList;

    /**
     * 专题关联ID列表
     */
    private List<Long> subjectIds;

    /**
     * 优选专区关联ID列表
     */
    private List<Long> preferenceAreaIds;

    // 更新标记（创建时忽略）

    /**
     * 是否更新SKU
     */
    private boolean updateSkus;

    /**
     * 是否更新属性值
     */
    private boolean updateAttrValues;

    /**
     * 是否更新满减规则
     */
    private boolean updateFullReductions;

    /**
     * 是否更新专题关联
     */
    private boolean updateSubjects;

    /**
     * 是否更新优选专区关联
     */
    private boolean updatePreferenceAreas;

    /**
     * 判断是否为创建操作
     */
    public boolean isCreate() {
        return spu == null || spu.getId() == null;
    }

    /**
     * 获取SPU ID（更新时使用）
     */
    public Long getSpuId() {
        return spu != null ? spu.getId() : null;
    }

    // SKU 聚合数据（内部类）

    /**
     * SKU聚合数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkuData {

        /**
         * SKU主表
         */
        private Sku sku;

        /**
         * SKU库存（创建时必需）
         */
        private SkuStock stock;

        /**
         * SKU促销信息（可选）
         */
        private SkuPromotion promotion;

        /**
         * SKU阶梯价列表（可选）
         */
        private List<SkuLadder> ladderList;

        /**
         * SKU会员价列表（可选）
         */
        private List<SkuMemberPrice> memberPriceList;

        // 更新标记

        /**
         * 是否更新促销信息
         */
        private boolean updatePromotion;

        /**
         * 是否更新阶梯价
         */
        private boolean updateLadders;

        /**
         * 是否更新会员价
         */
        private boolean updateMemberPrices;

        /**
         * 判断是否为新增SKU
         */
        public boolean isCreate() {
            return sku == null || sku.getId() == null;
        }
    }
}
