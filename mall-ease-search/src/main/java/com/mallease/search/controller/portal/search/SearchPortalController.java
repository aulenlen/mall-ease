package com.mallease.search.controller.portal.search;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.search.controller.portal.search.vo.SpuSearchPageRespVO;
import com.mallease.search.convert.SearchResultConvert;
import com.mallease.search.service.search.SpuSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 搜索前台 Controller
 */
@Tag(name = "商品搜索", description = "商品搜索、筛选、建议")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchPortalController {

    private final SpuSearchService spuSearchService;
    private final SearchResultConvert searchResultConvert;

    @Operation(summary = "搜索建议")
    @GetMapping("/suggest")
    public R<List<String>> suggest(@RequestParam String prefix, @RequestParam(defaultValue = "10") Integer size) {
        return R.success(spuSearchService.suggest(prefix, size));
    }

    @Operation(summary = "前台商品搜索", description = "支持关键词、分类、品牌、价格区间、规格筛选")
    @PostMapping("/portal/product")
    public R<SpuSearchPageRespVO> portalSearch(@Validated @RequestBody SpuSearchQuery reqVO) {
        if (reqVO.getNeedAggregation() == null) {
            reqVO.setNeedAggregation(true);
        }
        SpuSearchResultDTO dto = spuSearchService.searchWithAggregation(reqVO);
        return R.success(searchResultConvert.toPageRespVO(dto));
    }
}