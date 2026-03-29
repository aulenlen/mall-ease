package com.mallease.product.feign.search;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuIndexDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "mall-ease-search")
public interface SpuSearchFeignClient {
    @PostMapping("/admin/search/indexes/batch")
    R<?> indexBatch(@RequestBody List<SpuIndexDTO> spuIndexDTOList);

    @Operation(summary = "下架商品", description = "内部调用，返回的是elasticsearch不存在的spuIds，若为空则全部更新成功")
    @PutMapping("/search/internal/product/unpublish")
    R<List<Long>> unpublish(@RequestBody List<Long> spuIds);

    @Operation(summary = "上架商品（仅更新状态）", description = "内部调用，返回的是elasticsearch不存在的spuIds，若为空则全部更新成功")
    @PutMapping("/search/internal/product/publish")
    R<List<Long>> publish(@RequestBody List<Long> spuIds);
}
