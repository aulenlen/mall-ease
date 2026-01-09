package com.mallease.app.controller;

import com.mallease.app.converter.ProductConverter;
import com.mallease.app.feign.ProductFeignClient;
import com.mallease.app.model.vo.ProductVO;
import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "App商品", description = "商品详情、列表、搜索")
@RestController
@RequestMapping("/app/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductFeignClient productFeignClient;
    private final ProductConverter productConverter;

    @Operation(summary = "商品详情")
    @GetMapping("/{spuId}")
    public R<ProductVO> getDetail(@PathVariable Long spuId) {
        ProductDTO productDTO = productFeignClient.getProduct(spuId).getData();
        return R.success(productConverter.dtoToVo(productDTO));
    }
}
