package com.mallease.product.controller.internal.spu;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SpuMatchQueryDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import com.mallease.product.controller.admin.spu.vo.SnapshotVO;
import com.mallease.product.convert.spu.SpuSnapshotConvert;
import com.mallease.product.service.spu.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Tag(name = "商品内部接口", description = "供 BFF、营销等服务调用")
@RestController
@RequiredArgsConstructor
@RequestMapping("/product/spu/internal")
public class SpuInternalController {

    private final SpuService spuService;
    private final SpuSnapshotConvert spuSnapshotConvert;

    @Operation(summary = "获取完整商品信息", description = "内部调用")
    @GetMapping("/{spuId}")
    public R<ProductDTO> getProduct(@PathVariable Long spuId) {
        SnapshotVO snapshot = spuService.getPublishedProductDetail(spuId);
        if (snapshot == null) {
            return R.success(null);
        }
        return R.success(spuSnapshotConvert.toProductDTO(snapshot));
    }

    @Operation(summary = "MySQL搜索商品", description = "内部调用")
    @PostMapping("/advancedSearch")
    public R<SpuSearchResultDTO> advancedSearch(@RequestBody SpuSearchQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        return R.success(spuService.searchSpus(query));
    }

    @Operation(summary = "按条件匹配SPU ID", description = "内部调用，在候选商品范围内返回匹配的SPU ID列表")
    @PostMapping("/matchIds")
    public R<List<Long>> matchIds(@RequestBody SpuMatchQueryDTO query) {
        return R.success(spuService.matchSpuIds(query));
    }

    @Operation(summary = "批量获取商品详情快照", description = "内部调用，返回商品详情缓存快照")
    @PostMapping("/detailSnapshots")
    public R<List<ProductDTO>> detailSnapshots(@RequestBody List<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return R.success(Collections.emptyList());
        }
        return R.success(spuService.listPublishedProductSnapshots(spuIds));
    }
}