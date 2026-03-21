package com.mallease.marketing.controller;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.marketing.cache.FlashCacheService;
import com.mallease.marketing.converter.FlashConverter;
import com.mallease.marketing.model.client.vo.FlashPortalProductVO;
import com.mallease.marketing.model.client.vo.FlashPortalSessionVO;
import com.mallease.marketing.model.client.vo.FlashProductVO;
import com.mallease.marketing.model.data.entity.FlashProduct;
import com.mallease.marketing.model.data.entity.FlashSession;
import com.mallease.marketing.model.enums.FlashSessionStatus;
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

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "秒杀前台查询")
@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/marketing/flash")
public class FlashPortalController {

    private final FlashService flashService;
    private final FlashConverter flashConverter;
    private final FlashCacheService flashCacheService;

    @Operation(summary = "获取当前秒杀数据")
    @GetMapping("/current")
    public R<FlashCurrentDTO> getCurrentFlashData() {
        return R.success(flashService.getCurrentFlashData());
    }

    @Operation(summary = "查询已发布场次列表")
    @GetMapping("/sessions")
    public R<List<FlashPortalSessionVO>> listSessions() {
        return R.success(flashConverter.sessionListToPortalVoList(
                flashService.listPublishedFlashSessions(LocalDateTime.now())
        ));
    }

    @Operation(summary = "根据场次查询秒杀商品列表")
    @GetMapping("/sessions/{sessionId:\\d+}/products")
    public R<List<FlashPortalProductVO>> listProductsBySession(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId) {
        ensurePublishedSession(sessionId);
        return R.success(toPortalProducts(flashService.listFlashProductBySessionId(sessionId)));
    }

    @Operation(summary = "获取秒杀商品详情")
    @GetMapping("/products/{id:\\d+}")
    public R<FlashPortalProductVO> getProduct(
            @Parameter(description = "秒杀商品ID", required = true) @PathVariable Long id) {
        FlashProduct product = flashService.getFlashProductById(id);
        ensurePublishedSession(product.getFlashSessionId());
        List<FlashPortalProductVO> products = toPortalProducts(List.of(product));
        return R.success(products.isEmpty() ? null : products.get(0));
    }

    @Operation(summary = "获取热点秒杀商品详情")
    @GetMapping("/detail/{spuId:\\d+}")
    public R<ProductDTO> getHotDetail(@Parameter(description = "SPU ID", required = true) @PathVariable Long spuId,
                                      @Parameter(description = "秒杀场次ID", required = true) @RequestParam Long sessionId) {
        return R.success(flashCacheService.getHotDetail(sessionId, spuId));
    }

    private void ensurePublishedSession(Long sessionId) {
        FlashSession session = flashService.getFlashSessionById(sessionId);
        if (!FlashSessionStatus.ENABLED.codeEquals(session.getSessionStatus())) {
            throw new ApiException("秒杀场次未上架");
        }
        if (session.getEndTime() != null && session.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ApiException("秒杀场次已结束");
        }
    }

    private List<FlashPortalProductVO> toPortalProducts(List<FlashProduct> products) {
        List<FlashProductVO> productVOS = flashService.enrichWithSkuInfo(products);
        return flashConverter.productVoListToPortalVoList(productVOS);
    }
}
