package com.mallease.product.controller.internal.sku;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.product.service.sku.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SKU内部接口
 */
@Tag(name = "SKU内部接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/sku")
public class SkuInternalController {

    private final SkuService skuService;

    @Operation(summary = "批量获取 SKU 简要信息", description = "内部服务调用，用于秒杀/优惠券等场景")
    @PostMapping("/listSimpleByIds")
    public R<List<SkuSimpleDTO>> listSimpleByIds(@RequestBody List<Long> skuIds) {
        return R.success(skuService.listSimpleByIds(skuIds));
    }
}
