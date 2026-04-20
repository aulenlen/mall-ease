package com.mallease.bff.convert;

import com.mallease.bff.controller.portal.article.vo.ArticleDetailRespVO;
import com.mallease.bff.controller.portal.common.vo.RecommendProductRespVO;
import com.mallease.common.dto.remote.ArticleDetailDTO;
import com.mallease.common.dto.remote.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** 文章详情转换器。 */
@Mapper(componentModel = "spring")
public interface ArticleConvert {

    @Mapping(target = "products", ignore = true)
    ArticleDetailRespVO toArticleDetailResp(ArticleDetailDTO dto);

    default ArticleDetailRespVO toArticleDetailResp(ArticleDetailDTO dto, List<ProductDTO> productList) {
        if (dto == null) {
            return null;
        }
        ArticleDetailRespVO respVO = toArticleDetailResp(dto);
        respVO.setProducts(toRecommendProductRespList(productList, dto.getSpuIds()));
        return respVO;
    }

    default List<RecommendProductRespVO> toRecommendProductRespList(List<ProductDTO> productList, List<Long> orderedSpuIds) {
        if (CollectionUtils.isEmpty(productList) || CollectionUtils.isEmpty(orderedSpuIds)) {
            return Collections.emptyList();
        }
        Map<Long, RecommendProductRespVO> productMap = productList.stream()
                .map(this::toRecommendProductResp)
                .filter(Objects::nonNull)
                .filter(product -> product.getSpuId() != null)
                .collect(Collectors.toMap(
                        RecommendProductRespVO::getSpuId,
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        Set<Long> distinctSpuIds = new LinkedHashSet<>(orderedSpuIds);
        List<RecommendProductRespVO> orderedList = new ArrayList<>();
        for (Long spuId : distinctSpuIds) {
            RecommendProductRespVO product = productMap.get(spuId);
            if (product != null) {
                orderedList.add(product);
            }
        }
        return orderedList;
    }

    default RecommendProductRespVO toRecommendProductResp(ProductDTO dto) {
        if (dto == null || dto.getSpu() == null) {
            return null;
        }
        ProductDTO.SpuInfo spu = dto.getSpu();
        ProductDTO.SpuSaleInfo sale = dto.getSale();
        return RecommendProductRespVO.builder()
                .spuId(spu.getId())
                .name(spu.getName())
                .subTitle(spu.getSubTitle())
                .categoryId(spu.getCategoryId())
                .categoryPath(spu.getCategoryIds())
                .pic(spu.getPic())
                .brandName(spu.getBrandName())
                .minPrice(sale != null ? sale.getMinPrice() : null)
                .maxPrice(sale != null ? sale.getMaxPrice() : null)
                .sale(sale != null ? sale.getTotalSale() : null)
                .isNew(Integer.valueOf(1).equals(spu.getNewStatus()))
                .build();
    }
}
