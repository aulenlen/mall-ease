package com.mallease.product.controller.portal.spu;

import com.mallease.common.api.R;
import com.mallease.product.controller.portal.spu.vo.ProductDetailRespVO;
import com.mallease.product.service.spu.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "前台商品查询", description = "前台商品详情查询")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/products")
public class SpuPortalController {

    private final SpuService spuService;

    @Operation(summary = "前台商品详情", description = "获取完整商品信息（前台展示）")
    @GetMapping("/{spuId}")
    public R<ProductDetailRespVO> portalDetail(@PathVariable Long spuId) {
        return R.success(spuService.getPortalDetail(spuId));
    }

    @Operation(summary = "前台秒杀商品详情", description = "获取非热点秒杀商品详情并覆盖秒杀价格")
    @GetMapping("/flash/{spuId}")
    public R<ProductDetailRespVO> flashPortalDetail(@PathVariable Long spuId, @RequestParam Long sessionId) {
        return R.success(spuService.getFlashPortalDetail(spuId, sessionId));
    }
}
