package com.mallease.pms.dto.context;

import com.mallease.pms.pojo.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKU创建数据（包装单个SKU及其所有关联数据）
 * <p>
 * 封装一个SKU的主表及其库存、促销、会员价、阶梯价等关联数据。
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuCreateData {

    /**
     * SKU主表（必需）
     */
    private PmsSku sku;

    /**
     * SKU库存（必需）
     */
    private PmsSkuStock stock;

    /**
     * SKU促销信息（可选，已过滤无效数据）
     */
    private PmsSkuPromotion promotion;

    /**
     * SKU阶梯价列表（可选，已过滤无效数据）
     */
    private List<PmsSkuLadder> ladderList;

    /**
     * SKU会员价列表（可选，已过滤无效数据）
     */
    private List<PmsSkuMemberPrice> memberPriceList;
}
