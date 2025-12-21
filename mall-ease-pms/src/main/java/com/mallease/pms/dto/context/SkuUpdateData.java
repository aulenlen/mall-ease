package com.mallease.pms.dto.context;

import com.mallease.pms.pojo.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKU更新数据（包装已转换的Entity）
 *
 * @author: Aulen
 * @create: 2025-12-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuUpdateData {

    /**
     * SKU主表（必需，包含ID表示更新，无ID表示新增）
     */
    private PmsSku sku;

    /**
     * SKU促销信息（可选更新）
     */
    private PmsSkuPromotion promotion;

    /**
     * SKU阶梯价列表（可选，传入表示全量替换）
     */
    private List<PmsSkuLadder> ladderList;

    /**
     * SKU会员价列表（可选，传入表示全量替换）
     */
    private List<PmsSkuMemberPrice> memberPriceList;

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
}