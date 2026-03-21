package com.mallease.product.converter;

import cn.hutool.core.util.StrUtil;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.product.model.client.vo.ProductVO;
import com.mallease.product.model.data.cache.SpuCache;
import com.mallease.product.model.data.entity.Brand;
import com.mallease.product.model.data.entity.Category;
import com.mallease.product.model.data.entity.Sku;
import com.mallease.product.model.data.entity.SkuStock;
import com.mallease.product.model.data.entity.Spu;
import com.mallease.product.model.data.entity.SpuDetail;
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
    @Mapping(target = "totalSale", source = "spu.sale")
    @Mapping(target = "minPrice", source = "spu.minPrice")
    @Mapping(target = "maxPrice", source = "spu.maxPrice")
    @Mapping(target = "inStock", source = "spu.inStock")
    SpuCache.SpuDetailInfo toSpuDetailInfo(SpuDetail detail, Spu spu);

    SpuCache.BrandInfo toBrandInfo(Brand brand);

    @Mapping(target = "id", source = "category.id")
    @Mapping(target = "name", source = "category.name")
    @Mapping(target = "categoryIds", source = "categoryIds")
    SpuCache.CategoryInfo toCategoryInfo(Category category, String categoryIds);

    SpuCache.SkuBasicInfo toSkuBasicInfo(Sku sku);

    @Mapping(target = "promotionPrice", ignore = true)
    SpuCache.SkuPriceInfo toSkuPriceInfo(Sku sku);

    SpuCache.SkuConfigInfo toSkuConfigInfo(SkuStock stock);

    // Cache → DTO（内部调用）

    /**
     * SpuCache → ProductDTO（服务间调用）
     */
    ProductDTO cacheToDTO(SpuCache cache);

    /**
     * SpuCache 列表 → ProductDTO 列表（服务间调用）
     */
    List<ProductDTO> cacheListToDTOList(List<SpuCache> caches);

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
