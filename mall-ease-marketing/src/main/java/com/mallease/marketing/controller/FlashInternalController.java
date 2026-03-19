package com.mallease.marketing.controller;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.SpuFlashOverlayDTO;
import com.mallease.marketing.cache.FlashCacheService;
import com.mallease.marketing.converter.FlashConverter;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.service.FlashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "秒杀内部接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/marketing/flash")
public class FlashInternalController {

    private final FlashService flashService;
    private final FlashConverter flashConverter;
    private final FlashCacheService flashCacheService;

    @Operation(summary = "获取当前秒杀数据", description = "供 BFF 或其他服务查询当前秒杀场次")
    @GetMapping("/current")
    public R<FlashCurrentDTO> getCurrentFlashData() {
        return R.success(flashService.getCurrentFlashData());
    }

    @Operation(summary = "获取秒杀商品快照", description = "供内部服务根据秒杀商品ID查询价格、库存和商品基础信息")
    @GetMapping("/products/{id:\\d+}")
    public R<FlashProductVO> getProductSnapshot(
            @Parameter(description = "秒杀商品ID", required = true) @PathVariable Long id) {
        FlashProduct product = flashService.getFlashProductById(id);
        List<FlashProductVO> products = flashService.enrichWithSkuInfo(List.of(product));
        return R.success(products.isEmpty() ? flashConverter.productToVo(product) : products.get(0));
    }

    @Operation(summary = "获取秒杀价格覆盖信息", description = "供商品服务合并秒杀价格与活动信息")
    @GetMapping("/overlay/{spuId:\\d+}")
    public R<SpuFlashOverlayDTO> getOverlay(
            @Parameter(description = "SPU ID", required = true) @PathVariable Long spuId,
            @Parameter(description = "秒杀场次ID", required = true) @RequestParam Long sessionId) {
        return R.success(flashCacheService.getOverlay(sessionId, spuId));
    }
}
