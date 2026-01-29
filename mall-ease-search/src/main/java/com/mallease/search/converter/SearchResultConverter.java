package com.mallease.search.converter;

import com.mallease.common.api.Page;
import com.mallease.common.dto.remote.SearchFilterDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.search.model.client.vo.SearchFilterVO;
import com.mallease.search.model.client.vo.SpuItemVO;
import com.mallease.search.model.client.vo.SpuSearchPageVO;
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
public interface SearchResultConverter {

    // 商品项转换

    @Mapping(target = "highlightName", ignore = true)
    @Mapping(target = "score", ignore = true)
    SpuItemVO dtoToVo(SpuRecommendDTO dto);

    List<SpuItemVO> dtoListToVoList(List<SpuRecommendDTO> dtoList);

    // 筛选面板转换

    @Mapping(target = "brands", source = "brands", qualifiedByName = "toBrandAggVOList")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "toCategoryAggVOList")
    @Mapping(target = "attrs", source = "attrs", qualifiedByName = "toAttrAggVOList")
    @Mapping(target = "priceRanges", source = "priceRange", qualifiedByName = "toPriceRangeVOList")
    SearchFilterVO filterDtoToVo(SearchFilterDTO dto);

    @Named("toBrandAggVOList")
    default List<SearchFilterVO.BrandAggVO> toBrandAggVOList(List<SearchFilterDTO.FilterItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(item -> SearchFilterVO.BrandAggVO.builder()
                        .brandId(item.getId())
                        .brandName(item.getName())
                        .count(item.getCount())
                        .build())
                .toList();
    }

    @Named("toCategoryAggVOList")
    default List<SearchFilterVO.CategoryAggVO> toCategoryAggVOList(List<SearchFilterDTO.FilterItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(item -> SearchFilterVO.CategoryAggVO.builder()
                        .categoryId(item.getId())
                        .categoryName(item.getName())
                        .count(item.getCount())
                        .build())
                .toList();
    }

    @Named("toAttrAggVOList")
    default List<SearchFilterVO.AttrAggVO> toAttrAggVOList(List<SearchFilterDTO.AttrFilterItem> items) {
        if (items == null) return Collections.emptyList();
        return items.stream()
                .map(item -> SearchFilterVO.AttrAggVO.builder()
                        .attrId(item.getAttrId())
                        .attrName(item.getAttrName())
                        .values(item.getValues() == null ? Collections.emptyList() :
                                item.getValues().stream()
                                        .map(v -> SearchFilterVO.AttrValueAggVO.builder()
                                                .value(v.getValue())
                                                .count(v.getCount())
                                                .build())
                                        .toList())
                        .build())
                .toList();
    }

    @Named("toPriceRangeVOList")
    default List<SearchFilterVO.PriceRangeVO> toPriceRangeVOList(SearchFilterDTO.PriceRange priceRange) {
        if (priceRange == null || priceRange.getMin() == null || priceRange.getMax() == null) {
            return Collections.emptyList();
        }

        BigDecimal min = priceRange.getMin();
        BigDecimal max = priceRange.getMax();

        // 生成价格区间桶（根据价格范围自动分桶）
        List<SearchFilterVO.PriceRangeVO> ranges = new ArrayList<>();
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

            ranges.add(SearchFilterVO.PriceRangeVO.builder()
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
            ranges.add(SearchFilterVO.PriceRangeVO.builder()
                    .key(lastThreshold.intValue() + "+")
                    .label("¥" + lastThreshold.intValue() + "+")
                    .from(lastThreshold)
                    .to(null)
                    .count(null)
                    .build());
        }

        return ranges;
    }

    default SpuSearchPageVO toPageVO(SpuSearchResultDTO dto) {
        SpuSearchPageVO result = new SpuSearchPageVO();

        if (dto == null) {
            result.setProducts(new Page<>());
            result.setFilters(new SearchFilterVO());
            return result;
        }

        // 转换商品列表
        Page<SpuItemVO> productPage = new Page<>();
        if (dto.getProducts() != null) {
            Page<SpuRecommendDTO> source = dto.getProducts();
            List<SpuItemVO> items = dtoListToVoList(source.getList());
            productPage.setList(items);
            productPage.setPageNum(source.getPageNum());
            productPage.setPageSize(source.getPageSize());
            productPage.setTotal(source.getTotal());
            productPage.setTotalPage(source.getTotalPage());
        }

        // 转换筛选面板
        SearchFilterVO filters = filterDtoToVo(dto.getFilters());

        result.setProducts(productPage);
        result.setFilters(filters != null ? filters : new SearchFilterVO());
        return result;
    }
}
