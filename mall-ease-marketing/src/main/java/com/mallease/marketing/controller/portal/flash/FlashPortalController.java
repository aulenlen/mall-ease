package com.mallease.marketing.controller.portal.flash;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalDetailRespVO;
import com.mallease.marketing.service.flash.FlashPreheatService;
import com.mallease.marketing.convert.FlashConvert;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalProductRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSelectorRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSessionRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSessionsRespVO;
import com.mallease.marketing.controller.portal.flash.vo.FlashPortalSkuSelectedRespVO;
import com.mallease.marketing.service.flash.FlashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "秒杀前台查询")
@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/flash")
public class FlashPortalController {

    private final FlashService flashService;
    private final FlashConvert flashConvert;
    private final FlashPreheatService flashPreheatService;

    @Operation(summary = "获取当前秒杀数据")
    @GetMapping("/current")
    public R<FlashCurrentDTO> getCurrentFlashData() {
        return R.success(flashService.getCurrentData());
    }

    @Operation(summary = "查询已发布场次列表")
    @GetMapping("/sessions")
    public R<FlashPortalSessionsRespVO> listSessions() {
        return R.success(flashPreheatService.getPortalSessions(LocalDate.now()));
    }

    @Operation(summary = "根据场次查询秒杀商品列表")
    @GetMapping("/sessions/{sessionId:\\d+}/products")
    public R<List<FlashPortalProductRespVO>> listProductsBySession(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId) {
        return R.success(flashPreheatService.getPortalProducts(sessionId));
    }

    @Operation(summary = "获取秒杀商品详情")
    @GetMapping("/products/{sessionId:\\d+}/{spuId:\\d+}")
    public R<FlashPortalDetailRespVO> getProduct(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId,
            @Parameter(description = "SPU ID", required = true) @PathVariable Long spuId) {
        return R.success(flashPreheatService.getPortalDetail(sessionId, spuId));
    }

    @Operation(summary = "获取秒杀商品规格选择器")
    @GetMapping("/products/{sessionId:\\d+}/{spuId:\\d+}/selector")
    public R<FlashPortalSelectorRespVO> getSelector(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId,
            @Parameter(description = "SPU ID", required = true) @PathVariable Long spuId) {
        return R.success(flashPreheatService.getPortalSelector(sessionId, spuId));
    }

    @Operation(summary = "获取秒杀SKU价格与库存")
    @GetMapping("/products/{sessionId:\\d+}/{spuId:\\d+}/sku-selected")
    public R<FlashPortalSkuSelectedRespVO> getSkuSelected(
            @Parameter(description = "秒杀场次ID", required = true) @PathVariable Long sessionId,
            @Parameter(description = "SPU ID", required = true) @PathVariable Long spuId,
            @Parameter(description = "SKU ID", required = true) @org.springframework.web.bind.annotation.RequestParam Long skuId) {
        return R.success(flashPreheatService.getPortalSkuSelected(sessionId, spuId, skuId));
    }
}
