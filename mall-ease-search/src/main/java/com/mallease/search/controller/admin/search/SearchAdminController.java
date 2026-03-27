package com.mallease.search.controller.admin.search;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuIndexDTO;
import com.mallease.search.convert.SpuIndexConvert;
import com.mallease.search.dal.entity.EsSpu;
import com.mallease.search.service.search.SpuSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 搜索管理 Controller
 */
@Tag(name = "搜索管理", description = "索引管理")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchAdminController {

    private final SpuSearchService spuSearchService;
    private final SpuIndexConvert spuIndexConvert;

    @Operation(summary = "重建索引")
    @PostMapping("/admin/rebuild")
    public R<Void> rebuildIndex() {
        spuSearchService.rebuildIndex();
        return R.success(null);
    }

    @Operation(summary = "保存索引")
    @PostMapping("/index/batch")
    public R<Boolean> indexBatch(@RequestBody List<SpuIndexDTO> dtoList) {
        List<EsSpu> entityList = spuIndexConvert.spuIndexDTOListToEsSpuList(dtoList);
        spuSearchService.indexBatch(entityList);
        return R.success(true);
    }
}