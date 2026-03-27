package com.mallease.search.controller.internal.search;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.search.convert.EsSpuConvert;
import com.mallease.search.dal.entity.EsSpu;
import com.mallease.search.service.search.SpuSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 搜索内部 Controller
 */
@Tag(name = "搜索内部接口", description = "搜索内部调用")
@RestController
@RequestMapping("/search/internal")
@RequiredArgsConstructor
public class SearchInternalController {

    private final SpuSearchService spuSearchService;
    private final EsSpuConvert esSpuConvert;

    @Operation(summary = "获取推荐商品", description = "内部调用，返回推荐商品列表（按销量排序、有货）")
    @GetMapping("/recommend")
    public R<List<SpuRecommendDTO>> listRecommend(@Parameter(description = "返回数量，默认20") @RequestParam(defaultValue = "20") Integer limit) {
        SpuSearchQuery reqVO = new SpuSearchQuery();
        reqVO.setInStock(true);
        reqVO.setSortType(1);
        reqVO.setPageNum(1);
        reqVO.setPageSize(limit);

        List<EsSpu> entityList = spuSearchService.search(reqVO);
        return R.success(esSpuConvert.entityListToDTOList(entityList));
    }

    @Operation(summary = "下架商品", description = "内部调用，返回的是elasticsearch不存在的spuIds，若为空则全部更新成功")
    @PutMapping("/product/unpublish")
    public R<List<Long>> unpublish(@RequestBody List<Long> spuIds) {
        return R.success(spuSearchService.unpublish(spuIds));
    }

    @Operation(summary = "上架商品（仅更新状态）", description = "内部调用，返回的是elasticsearch不存在的spuIds，若为空则全部更新成功")
    @PutMapping("/product/publish")
    public R<List<Long>> publish(@RequestBody List<Long> spuIds) {
        return R.success(spuSearchService.publish(spuIds));
    }

    @Operation(summary = "商品搜索（带筛选面板）")
    @PostMapping("/product/advancedSearch")
    public R<SpuSearchResultDTO> advancedSearch(@Validated @RequestBody SpuSearchQuery reqVO) {
        return R.success(spuSearchService.searchWithAggregation(reqVO));
    }
}