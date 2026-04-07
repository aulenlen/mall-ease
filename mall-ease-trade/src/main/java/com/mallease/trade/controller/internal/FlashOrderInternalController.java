package com.mallease.trade.controller.internal;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.FlashCreateOrderReqDTO;
import com.mallease.common.dto.remote.FlashCreateOrderRespDTO;
import com.mallease.trade.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/trade/orders/flash")
public class FlashOrderInternalController {

    private final OrderService orderService;

    @PostMapping
    public R<FlashCreateOrderRespDTO> create(@Validated @RequestBody FlashCreateOrderReqDTO req) {
        return R.success(orderService.createFlashOrder(req));
    }
}

