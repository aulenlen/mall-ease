package com.mallease.pms.converter;

import com.mallease.pms.dto.cache.PmsBrandCacheDTO;
import com.mallease.pms.dto.cache.PmsProductBasicCacheDTO;
import com.mallease.pms.dto.vo.*;
import com.mallease.pms.pojo.*;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author: Aulen
 * @description: 商品缓存数据转换器 用于将实体对象转换为缓存 DTO 和 VO
 * @create: 2025-11-21
 **/
@Mapper(componentModel = "spring")
public interface PmsProductCacheConverter {

    PmsProductBasicCacheDTO productToBasicCache(PmsProduct pmsProduct);

    List<PmsProductBasicCacheDTO> productListToBasicCacheList(List<PmsProduct> products);

    PmsBrandCacheDTO brandToCache(PmsBrand brand);

    List<PmsBrandCacheDTO> brandListToCacheList(List<PmsBrand> brands);

    PmsProductAttributeVO attributeToVo(PmsProductAttribute attribute);

    List<PmsProductAttributeVO> attributeListToVo(List<PmsProductAttribute> attributes);

    PmsProductAttributeValueVO attributeValueToVo(PmsProductAttributeValue attributeValue);

    List<PmsProductAttributeValueVO> attributeValueListToVoList(List<PmsProductAttributeValue> attributeValues);

    PmsSkuStockVO skuToVo(PmsSkuStock sku);

    List<PmsSkuStockVO> skuListToVoList(List<PmsSkuStock> skus);

    PmsProductLadderVO ladderToVo(PmsProductLadder ladder);

    List<PmsProductLadderVO> ladderListToVoList(List<PmsProductLadder> ladders);

    PmsProductFullReductionVO reductionToVo(PmsProductFullReduction reduction);

    List<PmsProductFullReductionVO> reductionListToVoList(List<PmsProductFullReduction> reductions);

    PmsMemberPriceVO memberPriceToVo(PmsMemberPrice memberPrice);

    List<PmsMemberPriceVO> memberPriceListToVoList(List<PmsMemberPrice> memberPrices);
}
