package com.mallease.product.converter;

import cn.hutool.core.util.StrUtil;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.product.model.client.vo.ProductVO;
import com.mallease.product.model.data.cache.SpuCache;
import com.mallease.product.model.data.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * SPU缓存转换器
 *
 * @author: Aulen
 * @create: 2025-12-22
 */
@Mapper(componentModel = "spring")
public interface SpuCacheConverter {

    @Mapping(target = "albumPicList", source = "albumPics", qualifiedByName = "splitAlbumPics")
    SpuCache.SpuBasicInfo toSpuBasicInfo(Spu spu);

    @Mapping(target = "detailTitle", source = "detail.detailTitle")
    @Mapping(target = "detailDesc", source = "detail.detailDesc")
    @Mapping(target = "serviceList", source = "detail.serviceIds", qualifiedByName = "splitServiceIds")
    @Mapping(target = "freightTemplateId", source = "spu.freightTemplateId")
    @Mapping(target = "totalSale", source = "spu.sale")
    @Mapping(target = "minPrice", source = "spu.minPrice")
    @Mapping(target = "maxPrice", source = "spu.maxPrice")
    @Mapping(target = "totalStock", source = "spu.stock")
    SpuCache.SpuDetailInfo toSpuDetailInfo(SpuDetail detail, Spu spu);

    SpuCache.BrandInfo toBrandInfo(Brand brand);

    @Mapping(target = "id", source = "category.id")
    @Mapping(target = "name", source = "category.name")
    @Mapping(target = "categoryIds", source = "categoryIds")
    SpuCache.CategoryInfo toCategoryInfo(Category category, String categoryIds);

    SpuCache.SkuBasicInfo toSkuBasicInfo(Sku sku);

    SpuCache.SkuPriceInfo toSkuPriceInfo(Sku sku);

    @Mapping(target = "type", source = "promotionType")
    @Mapping(target = "price", source = "promotionPrice")
    @Mapping(target = "startTime", source = "promotionStartTime")
    @Mapping(target = "endTime", source = "promotionEndTime")
    @Mapping(target = "perLimit", source = "promotionPerLimit")
    SpuCache.SkuPromotionInfo toSkuPromotionInfo(SkuPromotion promotion);

    SpuCache.SkuBenefitInfo toSkuBenefitInfo(SkuPromotion promotion);

    SpuCache.SkuConfigInfo toSkuConfigInfo(SkuStock stock);

    SpuCache.FullReductionInfo toFullReductionInfo(SpuFullReduction reduction);

    List<SpuCache.FullReductionInfo> toFullReductionInfoList(List<SpuFullReduction> reductionList);

    // Cache → DTO（内部调用）

    /**
     * SpuCache → ProductDTO（服务间调用）
     */
    ProductDTO cacheToDTO(SpuCache cache);

    // Cache → VO（前台接口）

    /**
     * SpuCache → ProductVO（前台展示）
     */
    ProductVO cacheToVO(SpuCache cache);

    // 工具方法

    @Named("splitAlbumPics")
    default List<String> splitAlbumPics(String albumPics) {
        return StrUtil.isNotBlank(albumPics)
                ? Arrays.asList(albumPics.split(","))
                : Collections.emptyList();
    }

    @Named("splitServiceIds")
    default List<String> splitServiceIds(String serviceIds) {
        return StrUtil.isNotBlank(serviceIds)
                ? Arrays.asList(serviceIds.split(","))
                : Collections.emptyList();
    }
}