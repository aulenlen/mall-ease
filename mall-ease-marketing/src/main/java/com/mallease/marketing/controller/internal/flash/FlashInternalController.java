package com.mallease.marketing.controller.internal.flash;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCurrentDTO;
import com.mallease.marketing.service.flash.FlashPreheatService;
import com.mallease.marketing.convert.FlashConvert;
import com.mallease.marketing.controller.admin.flash.vo.FlashProductRespVO;
import com.mallease.marketing.dal.entity.FlashProduct;
import com.mallease.marketing.service.flash.FlashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "秒杀内部接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/marketing/flash")
public class FlashInternalController {

    private final FlashService flashService;
    private final FlashConvert flashConvert;
    private final FlashPreheatService flashPreheatService;

    @Operation(summary = "获取当前秒杀数据", description = "供 BFF 或其他服务查询当前秒杀场次")
    @GetMapping("/current")
    public R<FlashCurrentDTO> getCurrentFlashData() {
        return R.success(flashService.getCurrentData());
    }

    @Operation(summary = "获取秒杀商品快照", description = "供内部服务根据秒杀商品ID查询价格、库存和商品基础信息")
    @GetMapping("/products/{id:\\d+}")
    public R<FlashProductRespVO> getProductSnapshot(
            @Parameter(description = "秒杀商品ID", required = true) @PathVariable Long id) {
        FlashProduct product = flashService.getProductById(id);
        List<FlashProductRespVO> productRespVOList = flashService.enrichWithSkuInfo(List.of(product));
        return R.success(productRespVOList.isEmpty() ? flashConvert.toFlashProductResp(product) : productRespVOList.get(0));
    }
}
