package com.mallease.pms.converter;

import cn.hutool.core.util.StrUtil;
import com.mallease.pms.dto.cache.PmsSpuCacheDTO;
import com.mallease.pms.pojo.*;
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
public interface PmsSpuCacheConverter {

    /**
     * PmsSpu -> SpuBasicInfo
     */
    @Mapping(target = "albumPicList", source = "albumPics", qualifiedByName = "splitAlbumPics")
    PmsSpuCacheDTO.SpuBasicInfo toSpuBasicInfo(PmsSpu spu);

    /**
     * PmsSpuDetail + PmsSpu -> SpuDetailInfo
     */
    @Mapping(target = "detailTitle", source = "detail.detailTitle")
    @Mapping(target = "detailDesc", source = "detail.detailDesc")
    @Mapping(target = "serviceList", source = "detail.serviceIds", qualifiedByName = "splitServiceIds")
    @Mapping(target = "freightTemplateId", source = "spu.freightTemplateId")
    @Mapping(target = "totalSale", source = "spu.sale")
    @Mapping(target = "minPrice", source = "spu.minPrice")
    @Mapping(target = "maxPrice", source = "spu.maxPrice")
    @Mapping(target = "totalStock", source = "spu.stock")
    PmsSpuCacheDTO.SpuDetailInfo toSpuDetailInfo(PmsSpuDetail detail, PmsSpu spu);

    /**
     * PmsBrand -> BrandInfo
     */
    PmsSpuCacheDTO.BrandInfo toBrandInfo(PmsBrand brand);

    /**
     * PmsCategory + categoryIds -> CategoryInfo
     */
    @Mapping(target = "id", source = "category.id")
    @Mapping(target = "name", source = "category.name")
    @Mapping(target = "categoryIds", source = "categoryIds")
    PmsSpuCacheDTO.CategoryInfo toCategoryInfo(PmsCategory category, String categoryIds);

    /**
     * PmsSku -> SkuBasicInfo
     */
    PmsSpuCacheDTO.SkuBasicInfo toSkuBasicInfo(PmsSku sku);

    /**
     * PmsSku -> SkuPriceInfo
     */
    PmsSpuCacheDTO.SkuPriceInfo toSkuPriceInfo(PmsSku sku);

    /**
     * PmsSkuPromotion -> SkuPromotionInfo
     */
    @Mapping(target = "type", source = "promotionType")
    @Mapping(target = "price", source = "promotionPrice")
    @Mapping(target = "startTime", source = "promotionStartTime")
    @Mapping(target = "endTime", source = "promotionEndTime")
    @Mapping(target = "perLimit", source = "promotionPerLimit")
    PmsSpuCacheDTO.SkuPromotionInfo toSkuPromotionInfo(PmsSkuPromotion promotion);

    /**
     * PmsSkuPromotion -> SkuBenefitInfo
     */
    PmsSpuCacheDTO.SkuBenefitInfo toSkuBenefitInfo(PmsSkuPromotion promotion);

    /**
     * PmsSkuStock -> SkuConfigInfo
     */
    PmsSpuCacheDTO.SkuConfigInfo toSkuConfigInfo(PmsSkuStock stock);

    /**
     * PmsSpuFullReduction -> FullReductionInfo
     */
    PmsSpuCacheDTO.FullReductionInfo toFullReductionInfo(PmsSpuFullReduction reduction);

    /**
     * List<PmsSpuFullReduction> -> List<FullReductionInfo>
     */
    List<PmsSpuCacheDTO.FullReductionInfo> toFullReductionInfoList(List<PmsSpuFullReduction> reductionList);

    /**
     * 分割相册图片字符串
     */
    @Named("splitAlbumPics")
    default List<String> splitAlbumPics(String albumPics) {
        return StrUtil.isNotBlank(albumPics)
                ? Arrays.asList(albumPics.split(","))
                : Collections.emptyList();
    }

    /**
     * 分割服务ID字符串
     */
    @Named("splitServiceIds")
    default List<String> splitServiceIds(String serviceIds) {
        return StrUtil.isNotBlank(serviceIds)
                ? Arrays.asList(serviceIds.split(","))
                : Collections.emptyList();
    }
}
