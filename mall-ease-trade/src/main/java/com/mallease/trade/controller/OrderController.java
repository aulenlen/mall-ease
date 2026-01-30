package com.mallease.trade.controller;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.OrderDTO;
import com.mallease.trade.converter.OrderConverter;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器（内部接口）
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Tag(name = "订单管理-内部接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderConverter orderConverter;

    @Operation(summary = "创建订单", description = "内部调用")
    @PostMapping("/internal/create")
    public R<String> create(@RequestBody OrderDTO orderDTO) {
        OrderAggregate aggregate = orderConverter.dtoToAggregate(orderDTO);
        String orderNo = orderService.create(aggregate);
        return R.success(orderNo);
    }

    @Operation(summary = "查询订单详情", description = "内部调用")
    @GetMapping("/internal/detail")
    public R<OrderDTO> getByOrderNo(@RequestParam String orderNo) {
        OrderAggregate aggregate = orderService.getByOrderNo(orderNo);
        return R.success(orderConverter.aggregateToDto(aggregate));
    }

    @Operation(summary = "查询用户订单列表", description = "内部调用")
    @GetMapping("/internal/list")
    public R<List<OrderDTO>> listByUserId(@RequestParam Long userId,
                                          @RequestParam(required = false) Integer status) {
        List<OrderAggregate> aggregates = orderService.listByUserId(userId, status);
        return R.success(orderConverter.aggregateListToDtoList(aggregates));
    }

    @Operation(summary = "取消订单", description = "内部调用")
    @PostMapping("/internal/cancel")
    public R<Boolean> cancel(@RequestParam String orderNo, @RequestParam Long userId) {
        return R.success(orderService.cancel(orderNo, userId));
    }
}
