package com.mallease.marketing.controller.portal.flash;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.exception.ApiException;
import com.mallease.marketing.service.flash.cache.FlashCacheService;
import com.mallease.marketing.convert.FlashConvert;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalProductRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSessionRespVO;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductRespVO;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.dal.entity.FlashSession;
import com.mallease.marketing.enums.FlashSessionStatus;
import com.mallease.marketing.service.flash.FlashService;
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
@RequestMapping("/portal/flash")
public class FlashPortalController {

    private final FlashService flashService;
    private final FlashConvert flashConvert;
    private final FlashCacheService flashCacheService;

    @Operation(summary = "获取当前秒杀数据")
    @GetMapping("/current")
    public R<FlashCurrentDTO> getCurrentFlashData() {
        return R.success(flashService.getCurrentData());
    }

    @Operation(summary = "查询已发布场次列表")
    @GetMapping("/sessions")
    public R<List<FlashPortalSessionRespVO>> listSessions() {
        return R.success(flashConvert.toFlashPortalSessionRespList(
                flashService.listPublishedSessions(LocalDateTime.now())
        ));
    }

    @Operation(summary = "根据场次查询秒杀商品列表")
    @GetMapping("/sessions/{sessionId:\\d+}/products")
    public R<List<FlashPortalProductRespVO>> listProductsBySession(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId) {
        ensurePublishedSession(sessionId);
        return R.success(toPortalProducts(flashService.listProductsBySessionId(sessionId)));
    }

    @Operation(summary = "获取秒杀商品详情")
    @GetMapping("/products/{id:\\d+}")
    public R<FlashPortalProductRespVO> getProduct(
            @Parameter(description = "秒杀商品ID", required = true) @PathVariable Long id) {
        FlashProduct product = flashService.getProductById(id);
        ensurePublishedSession(product.getFlashSessionId());
        List<FlashPortalProductRespVO> productRespVOList = toPortalProducts(List.of(product));
        return R.success(productRespVOList.isEmpty() ? null : productRespVOList.get(0));
    }

    @Operation(summary = "获取热点秒杀商品详情")
    @GetMapping("/detail/{spuId:\\d+}")
    public R<ProductDTO> getHotDetail(@Parameter(description = "SPU ID", required = true) @PathVariable Long spuId,
                                      @Parameter(description = "秒杀场次ID", required = true) @RequestParam Long sessionId) {
        return R.success(flashCacheService.getHotDetail(sessionId, spuId));
    }

    private void ensurePublishedSession(Long sessionId) {
        FlashSession session = flashService.getSessionById(sessionId);
        if (!FlashSessionStatus.ENABLED.codeEquals(session.getSessionStatus())) {
            throw new ApiException("秒杀场次未上架");
        }
        if (session.getEndTime() != null && session.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ApiException("秒杀场次已结束");
        }
    }

    private List<FlashPortalProductRespVO> toPortalProducts(List<FlashProduct> products) {
        List<FlashProductRespVO> productRespVOList = flashService.enrichWithSkuInfo(products);
        return flashConvert.toFlashPortalProductRespList(productRespVOList);
    }
}
