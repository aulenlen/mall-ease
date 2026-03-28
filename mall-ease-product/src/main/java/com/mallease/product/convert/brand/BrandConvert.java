package com.mallease.product.convert.brand;

import com.mallease.common.dto.remote.BrandDTO;
import com.mallease.product.controller.admin.brand.vo.BrandDetailRespVO;
import com.mallease.product.controller.admin.brand.vo.BrandListRespVO;
import com.mallease.product.controller.admin.brand.vo.BrandSaveReqVO;
import com.mallease.product.dal.entity.Brand;
import com.mallease.product.dal.entity.CategoryBrandRelation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 品牌转换器
 */
@Mapper(componentModel = "spring")
public interface BrandConvert {

    BrandListRespVO toBrandListResp(Brand entity);

    BrandDetailRespVO toBrandDetailResp(Brand entity);

    List<BrandListRespVO> toBrandListRespList(List<Brand> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spuCount", ignore = true)
    @Mapping(target = "spuCommentCount", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "updater", ignore = true)
    Brand toBrand(BrandSaveReqVO reqVO);

    List<BrandDTO> toBrandRemoteList(List<Brand> brands);

    default CategoryBrandRelation toCategoryBrandRelation(Long categoryId, Long brandId) {
        CategoryBrandRelation entity = new CategoryBrandRelation();
        entity.setCategoryId(categoryId);
        entity.setBrandId(brandId);
        return entity;
    }

    default List<CategoryBrandRelation> toCategoryBrandRelationList(Long categoryId, List<Long> brandIds) {
        if (CollectionUtils.isEmpty(brandIds)) {
            return Collections.emptyList();
        }
        return brandIds.stream()
                .map(brandId -> toCategoryBrandRelation(categoryId, brandId))
                .toList();
    }
}