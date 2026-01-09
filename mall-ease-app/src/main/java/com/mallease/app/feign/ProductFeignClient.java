package com.mallease.app.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import com.mallease.common.dto.remote.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}