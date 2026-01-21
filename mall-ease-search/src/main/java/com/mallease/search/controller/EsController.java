package com.mallease.search.controller;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuIndexDTO;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.search.converter.SpuDocConverter;
import com.mallease.search.converter.SpuIndexConverter;
import com.mallease.search.model.data.doc.SpuDocument;
import com.mallease.search.model.client.vo.SpuSearchPageVO;
import com.mallease.search.model.client.vo.SpuSearchResultVO;
import com.mallease.search.service.SpuSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品搜索 Controller
 *
 * @author: Aulen
 * @create: 2025-12-24
 */
@Tag(name = "商品搜索", description = "商品搜索、筛选、建议")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SpuSearchController {

    private final SpuSearchService spuSearchService;
    private final SpuIndexConverter spuIndexConverter;
    private final SpuDocConverter spuDocConverter;

    @Operation(summary = "搜索建议")
    @GetMapping("/suggest")
    public R<List<String>> suggest(@RequestParam String prefix,
                                   @RequestParam(defaultValue = "10") Integer size) {
        return R.success(spuSearchService.suggest(prefix, size));
    }

    @Operation(summary = "重建索引")
    @PostMapping("/admin/rebuild")
    public R<Void> rebuildIndex() {
        spuSearchService.rebuildIndex();
        return R.success(null);
    }

    @Operation(summary = "保存索引")
    @PostMapping("/index/batch")
    public R<?> indexBatch(@RequestBody List<SpuIndexDTO> spuIndexDTOList) {
        List<SpuDocument> spuDocumentList = spuIndexConverter.spuIndexDTOListToDocList(spuIndexDTOList);
        spuSearchService.indexBatch(spuDocumentList);
        return R.success(true);
    }

    // 内部接口

    @Operation(summary = "获取推荐商品", description = "内部调用，返回推荐商品列表（按销量排序、有货）")
    @GetMapping("/internal/recommend")
    public R<List<SpuRecommendDTO>> listRecommend(
            @Parameter(description = "返回数量，默认20") @RequestParam(defaultValue = "20") Integer limit) {
        SpuSearchQuery query = new SpuSearchQuery();
        query.setInStock(true);
        query.setSortType(1);
        query.setPageNum(1);
        query.setPageSize(limit);

        List<SpuDocument> docs = spuSearchService.search(query);
        return R.success(spuDocConverter.docListToDTOList(docs));
    }

    @Operation(summary = "下架商品", description = "内部调用，返回的是elasticsearch不存在的spuIds，若为空则全部更新成功")
    @PutMapping("/internal/product/unpublish")
    public R<List<Long>> unpublish(@RequestBody List<Long> spuIds) {
        return R.success(spuSearchService.unpublish(spuIds));
    }

    @Operation(summary = "上架商品（仅更新状态）", description = "内部调用，返回的是elasticsearch不存在的spuIds，若为空则全部更新成功")
    @PutMapping("/internal/product/publish")
    public R<List<Long>> publish(@RequestBody List<Long> spuIds) {
        return R.success(spuSearchService.publish(spuIds));
    }

    @Operation(summary = "商品搜索（带筛选面板）")
    @PostMapping("/internal/product/advancedSearch")
    public R<SpuSearchPageVO> advancedSearch(@Validated @RequestBody SpuSearchQuery query) {

        if (Boolean.TRUE.equals(query.getNeedAggregation())) {
            return R.success(spuSearchService.searchWithAggregation(query));
        }

        List<SpuDocument> docs = spuSearchService.search(query);
        List<SpuSearchResultVO> voList = spuDocConverter.docListToVoList(docs);
        return R.success(SpuSearchPageVO.builder()
                .total((long) voList.size())
                .list(voList)
                .build());
    }
}