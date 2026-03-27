package com.mallease.bff.feign.search;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import com.mallease.common.dto.remote.SpuSearchQuery;
import com.mallease.common.dto.remote.SpuSearchResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 搜索服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@FeignClient(name = "mall-ease-search")
public interface SearchFeignClient {

    /**
     * 获取推荐商品
     *
     * @param limit 返回数量
     * @return 推荐商品列表
     */
    @GetMapping("/search/internal/recommend")
    R<List<SpuRecommendDTO>> listRecommend(@RequestParam(defaultValue = "20") Integer limit);

    @Operation(summary = "商品搜索（带筛选面板）")
    @PostMapping("/search/internal/product/advancedSearch")
    R<SpuSearchResultDTO> advancedSearch(@Validated @RequestBody SpuSearchQuery query);
}