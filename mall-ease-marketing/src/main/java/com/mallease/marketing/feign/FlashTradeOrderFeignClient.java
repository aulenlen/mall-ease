package com.mallease.marketing.feign;


import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCreateOrderReqDTO;
import com.mallease.common.dto.remote.FlashCreateOrderRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mall-ease-trade")
public interface FlashTradeOrderFeignClient {

    @PostMapping("/internal/trade/orders/flash")
    R<FlashCreateOrderRespDTO> createFlashOrder(@RequestBody FlashCreateOrderReqDTO req);
}

