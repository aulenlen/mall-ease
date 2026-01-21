package com.mallease.app.controller;

import com.mallease.app.converter.ProductConverter;
import com.mallease.app.feign.ProductFeignClient;
import com.mallease.app.feign.SearchFeignClient;
import com.mallease.app.model.vo.ProductSearchResultVO;
import com.mallease.app.model.vo.ProductVO;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "App商品", description = "商品详情、列表、搜索")
@RestController
@RequestMapping("/app/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductFeignClient productFeignClient;
    private final ProductConverter productConverter;
    private final SearchFeignClient searchFeignClient;

    @Operation(summary = "商品详情")
    @GetMapping("/{spuId}")
    public R<ProductVO> getDetail(@PathVariable Long spuId) {
        ProductDTO productDTO = productFeignClient.getProduct(spuId).getData();
        return R.success(productConverter.dtoToVo(productDTO));
    }

    @Operation(summary = "商品搜索", description = "支持关键词、分类、品牌、价格区间、规格筛选，返回聚合筛选项")
    @PostMapping("/search")
    public R<ProductSearchResultVO> search(@Validated @RequestBody SpuSearchQuery query) {

        if (query.getNeedAggregation() == null) {
            query.setNeedAggregation(true);
        }
        SpuSearchResultDTO resultDTO = searchFeignClient.advancedSearch(query).getData();
//        SpuSearchResultDTO resultDTO = productFeignClient.advancedSearch(query).getData();
        return R.success(productConverter.searchResultDtoToVo(resultDTO));
    }
}
