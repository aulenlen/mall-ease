package com.mallease.marketing.controller.portal.flash;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.common.dto.remote.FlashRestoreReqDTO;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.marketing.controller.portal.flash.vo.*;
import com.mallease.marketing.service.flash.FlashOrderService;
import com.mallease.marketing.service.flash.FlashPortalCacheService;
import com.mallease.marketing.convert.FlashConvert;
import com.mallease.marketing.service.flash.FlashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "秒杀前台查询")
@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/flash")
public class FlashPortalController {

    private final FlashService flashService;
    private final FlashPortalCacheService flashPortalCacheService;
    private final FlashOrderService flashOrderService;

    @Operation(summary = "获取当前秒杀数据")
    @GetMapping("/current")
    public R<FlashCurrentDTO> getCurrentFlashData() {
        return R.success(flashService.getCurrentData());
    }

    @Operation(summary = "查询已发布场次列表")
    @GetMapping("/sessions")
    public R<FlashPortalSessionsRespVO> listSessions() {
        return R.success(flashPortalCacheService.getPortalSessions(LocalDate.now()));
    }

    @Operation(summary = "根据场次查询秒杀商品列表")
    @GetMapping("/sessions/{sessionId:\\d+}/products")
    public R<List<FlashPortalProductRespVO>> listProductsBySession(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId) {
        return R.success(flashPortalCacheService.getPortalProducts(sessionId));
    }

    @Operation(summary = "获取秒杀商品详情")
    @GetMapping("/products/{sessionId:\\d+}/{spuId:\\d+}")
    public R<FlashPortalDetailRespVO> getProduct(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId,
            @Parameter(description = "SPU ID", required = true) @PathVariable Long spuId) {
        return R.success(flashPortalCacheService.getPortalDetail(sessionId, spuId));
    }

    @Operation(summary = "获取秒杀商品规格选择器")
    @GetMapping("/products/{sessionId:\\d+}/{spuId:\\d+}/selector")
    public R<FlashPortalSelectorRespVO> getSelector(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId,
            @Parameter(description = "SPU ID", required = true) @PathVariable Long spuId) {
        return R.success(flashPortalCacheService.getPortalSelector(sessionId, spuId));
    }

    @Operation(summary = "获取秒杀SKU价格与库存")
    @GetMapping("/products/{sessionId:\\d+}/{spuId:\\d+}/sku-selected")
    public R<FlashPortalSkuSelectedRespVO> getSkuSelected(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId,
            @Parameter(description = "SPU ID", required = true) @PathVariable Long spuId,
            @Parameter(description = "SKU ID", required = true) @org.springframework.web.bind.annotation.RequestParam Long skuId) {
        return R.success(flashPortalCacheService.getPortalSkuSelected(sessionId, spuId, skuId));
    }

    @Operation(summary = "提交秒杀订单")
    @PostMapping("/orders")
    public R<OrderSubmitRespVO> submitFlashOrder(@Validated @RequestBody FlashOrderSubmitReqVO reqVO) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(flashOrderService.submitFlashOrder(userId, reqVO));
    }

    @Operation(summary = "确认秒杀订单")
    @GetMapping("/confirm")
    public R<FlashOrderConfirmRespVO> confirm(@RequestParam Long sessionId, @RequestParam Long spuId,
                                              @RequestParam Long skuId, @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(flashOrderService.confirmOrder(userId, sessionId, spuId, skuId, quantity));
    }
}
