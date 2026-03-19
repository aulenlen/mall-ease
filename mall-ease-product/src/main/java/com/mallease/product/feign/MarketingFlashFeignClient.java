package com.mallease.product.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SpuFlashOverlayDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mall-ease-marketing", contextId = "marketingFlashFeignClient")
public interface MarketingFlashFeignClient {

    @GetMapping("/internal/marketing/flash/overlay/{spuId}")
    R<SpuFlashOverlayDTO> getOverlay(@PathVariable("spuId") Long spuId, @RequestParam("sessionId") Long sessionId);
}
