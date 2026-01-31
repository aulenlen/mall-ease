package com.mallease.bff.controller;

import com.mallease.bff.feign.TradeFeignClient;
import com.mallease.common.api.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BFF 订单控制器（前端接口）
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Tag(name = "BFF订单", description = "订单相关接口")
@RestController
@RequestMapping("/bff/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final TradeFeignClient tradeFeignClient;

    @GetMapping("/confirm")
    public R<?> confirm() {
        
        return null;
    }
}
