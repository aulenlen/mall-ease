package com.mallease.search.convert;

import com.mallease.common.api.Page;
import com.mallease.common.dto.remote.SearchFilterDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.search.controller.portal.search.vo.SearchFilterRespVO;
import com.mallease.search.controller.portal.search.vo.SpuItemRespVO;
import com.mallease.search.controller.portal.search.vo.SpuSearchPageRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 搜索结果转换器（DTO → VO）
 *
 * @author: Aulen
 * @create: 2026-01-28
 */
@Mapper(componentModel = "spring")
public interface SearchResultConvert {

    // 商品项转换

    @Mapping(target = "highlightName", ignore = true)
    @Mapping(target = "score", ignore = true)
    SpuItemRespVO dtoToRespVO(SpuRecommendDTO dto);

    List<SpuItemRespVO> dtoListToRespVOList(List<SpuRecommendDTO> dtoList);

    // 筛选面板转换

    @Mapping(target = "brands", source = "brands", qualifiedByName = "toBrandAggRespVOList")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "toCategoryAggRespVOList")
    @Mapping(target = "attrs", source = "attrs", qualifiedByName = "toAttrAggRespVOList")
    @Mapping(target = "priceRanges", source = "priceRange", qualifiedByName = "toPriceRangeRespVOList")
    SearchFilterRespVO filterDtoToRespVO(SearchFilterDTO dto);

    @Named("toBrandAggRespVOList")
    default List<SearchFilterRespVO.BrandAggRespVO> toBrandAggRespVOList(List<SearchFilterDTO.FilterItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(item -> SearchFilterRespVO.BrandAggRespVO.builder()
                        .brandId(item.getId())
                        .brandName(item.getName())
                        .count(item.getCount())
                        .build())
                .toList();
    }

    @Named("toCategoryAggRespVOList")
    default List<SearchFilterRespVO.CategoryAggRespVO> toCategoryAggRespVOList(List<SearchFilterDTO.FilterItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(item -> SearchFilterRespVO.CategoryAggRespVO.builder()
                        .categoryId(item.getId())
                        .categoryName(item.getName())
                        .count(item.getCount())
                        .build())
                .toList();
    }

    @Named("toAttrAggRespVOList")
    default List<SearchFilterRespVO.AttrAggRespVO> toAttrAggRespVOList(List<SearchFilterDTO.AttrFilterItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(item -> SearchFilterRespVO.AttrAggRespVO.builder()
                        .attrId(item.getAttrId())
                        .attrName(item.getAttrName())
                        .values(item.getValues() == null ? Collections.emptyList() :
                                item.getValues().stream()
                                        .map(v -> SearchFilterRespVO.AttrValueAggRespVO.builder()
                                                .value(v.getValue())
                                                .count(v.getCount())
                                                .build())
                                        .toList())
                        .build())
                .toList();
    }

    @Named("toPriceRangeRespVOList")
    default List<SearchFilterRespVO.PriceRangeRespVO> toPriceRangeRespVOList(SearchFilterDTO.PriceRange priceRange) {
        if (priceRange == null || priceRange.getMin() == null || priceRange.getMax() == null) {
            return Collections.emptyList();
        }

        BigDecimal min = priceRange.getMin();
        BigDecimal max = priceRange.getMax();

        // 生成价格区间桶（根据价格范围自动分桶）
        List<SearchFilterRespVO.PriceRangeRespVO> ranges = new ArrayList<>();
        BigDecimal[] thresholds = {
                BigDecimal.ZERO,
                new BigDecimal("100"),
                new BigDecimal("300"),
                new BigDecimal("500"),
                new BigDecimal("1000"),
                new BigDecimal("2000"),
                new BigDecimal("5000"),
                new BigDecimal("10000")
        };

        for (int i = 0; i < thresholds.length - 1; i++) {
            BigDecimal from = thresholds[i];
            BigDecimal to = thresholds[i + 1];

            if (from.compareTo(max) > 0) break;
            if (to.compareTo(min) < 0) continue;

            String key = from.intValue() + "-" + to.intValue();
            String label = "¥" + from.intValue() + "-" + to.intValue();

            ranges.add(SearchFilterRespVO.PriceRangeRespVO.builder()
                    .key(key)
                    .label(label)
                    .from(from)
                    .to(to)
                    .count(null)
                    .build());
        }

        // 添加最高价格区间
        BigDecimal lastThreshold = thresholds[thresholds.length - 1];
        if (max.compareTo(lastThreshold) >= 0) {
            ranges.add(SearchFilterRespVO.PriceRangeRespVO.builder()
                    .key(lastThreshold.intValue() + "+")
                    .label("¥" + lastThreshold.intValue() + "+")
                    .from(lastThreshold)
                    .to(null)
                    .count(null)
                    .build());
        }

        return ranges;
    }

    default SpuSearchPageRespVO toPageRespVO(SpuSearchResultDTO dto) {
        SpuSearchPageRespVO result = new SpuSearchPageRespVO();

        if (dto == null) {
            result.setProducts(new Page<>());
            result.setFilters(new SearchFilterRespVO());
            return result;
        }

        // 转换商品列表
        Page<SpuItemRespVO> productPage = new Page<>();
        if (dto.getProducts() != null) {
            Page<SpuRecommendDTO> source = dto.getProducts();
            List<SpuItemRespVO> items = dtoListToRespVOList(source.getList());
            productPage.setList(items);
            productPage.setPageNum(source.getPageNum());
            productPage.setPageSize(source.getPageSize());
            productPage.setTotal(source.getTotal());
            productPage.setTotalPage(source.getTotalPage());
        }

        // 转换筛选面板
        SearchFilterRespVO filters = filterDtoToRespVO(dto.getFilters());

        result.setProducts(productPage);
        result.setFilters(filters != null ? filters : new SearchFilterRespVO());
        return result;
    }
}
