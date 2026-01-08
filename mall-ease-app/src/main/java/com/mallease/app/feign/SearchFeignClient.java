package com.mallease.app.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuRecommendDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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
}