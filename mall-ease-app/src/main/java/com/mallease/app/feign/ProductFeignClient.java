package com.mallease.app.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.CategoryDTO;
import com.mallease.common.dto.remote.CategoryTreeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

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
}