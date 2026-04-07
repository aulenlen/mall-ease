package com.mallease.trade.feign.product;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashRestoreReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mall-ease-marketing")
public interface MarketingFlashFeignClient {
    @PostMapping("/internal/marketing/flash/restore-stock")
    R<Long> restoreStock(@RequestBody FlashRestoreReqDTO req);
}
