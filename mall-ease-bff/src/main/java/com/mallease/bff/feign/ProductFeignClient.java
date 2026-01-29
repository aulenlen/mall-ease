package com.mallease.bff.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@FeignClient(name = "mall-ease-product")
public interface ProductFeignClient {

    /**
     * 获取金刚区分类
     *
     * @return 分类列表
     */
    @GetMapping("/product/category/internal/nav")
    R<List<CategoryDTO>> listNavCategories();

    /**
     * 完整分类树
     *
     * @return 分类树列表
     */
    @GetMapping("/product/category/internal/portalTree")
    R<List<CategoryTreeDTO>> portalTree();

    /**
     * 通过spuId 获取完整的商品信息
     *
     * @param spuId spuId
     * @return 完整的商品信息
     */
    @GetMapping("/product/spu/internal/{spuId}")
    R<ProductDTO> getProduct(@PathVariable Long spuId);

    /**
     * MySQL 商品搜索（支持聚合筛选）
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @PostMapping("/product/spu/internal/advancedSearch")
    R<SpuSearchResultDTO> advancedSearch(@RequestBody SpuSearchQuery query);

    /**
     * 批量获取SKU简要信息
     *
     * @param skuIds SKU ID列表
     * @return SKU简要信息列表
     */
    @PostMapping("/product/sku/internal/listSimpleByIds")
    R<List<SkuSimpleDTO>> listSkuSimpleByIds(@RequestBody List<Long> skuIds);
}