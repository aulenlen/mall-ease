package com.mallease.search.controller;

import com.mallease.common.api.R;
import com.mallease.common.dto.SpuIndexDTO;
import com.mallease.search.converter.SpuIndexConverter;
import com.mallease.search.document.SpuDocument;
import com.mallease.search.dto.query.SpuSearchQuery;
import com.mallease.search.dto.vo.SearchPageVO;
import com.mallease.search.dto.vo.SpuSearchResultVO;
import com.mallease.search.service.SpuSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SpuSearchService spuSearchService;
    @Autowired
    private SpuIndexConverter spuIndexConverter;

    @Operation(summary = "商品搜索")
    @PostMapping("/spu")
    public R<SearchPageVO<SpuSearchResultVO>> search(@Validated @RequestBody SpuSearchQuery query) {
        return R.success(spuSearchService.search(query));
    }

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
}
