package com.mallease.trade.controller;

import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.converter.OrderConverter;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.client.cmd.SubmitOrderCmd;
import com.mallease.trade.model.client.vo.OrderConfirmVO;
import com.mallease.trade.model.client.vo.OrderVO;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Tag(name = "订单管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trade/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderConverter orderConverter;

    @Operation(summary = "订单确认页", description = "生成结算快照，返回确认页数据")
    @GetMapping("/portal/confirm")
    public R<OrderConfirmVO> confirm(@RequestParam List<Long> cartItemIds) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(orderService.generateSnapshot(userId, cartItemIds));
    }

    @Operation(summary = "提交订单")
    @PostMapping("/portal/submit")
    public R<String> submit(@Validated @RequestBody SubmitOrderCmd cmd) {
        Long userId = LoginContextUtil.getUserId();
        Order order = orderConverter.cmdToEntity(cmd);
        String orderNo = orderService.submit(userId, order, cmd.getRequestId());
        return R.success(orderNo);
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/portal/list")
    public R<Page<OrderVO>> portalList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = LoginContextUtil.getUserId();
        Page<OrderAggregate> aggregatePage = orderService.listByUserId(userId, status, pageNum, pageSize);
        List<OrderVO> voList = orderConverter.aggregatesToVOs(aggregatePage.getList());

        Page<OrderVO> result = new Page<>();
        result.setPageNum(aggregatePage.getPageNum());
        result.setPageSize(aggregatePage.getPageSize());
        result.setTotal(aggregatePage.getTotal());
        result.setTotalPage(aggregatePage.getTotalPage());
        result.setList(voList);
        return R.success(result);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/portal/detail")
    public R<OrderVO> portalDetail(@RequestParam String orderNo) {
        Long userId = LoginContextUtil.getUserId();
        OrderAggregate aggregate = orderService.getByOrderNo(orderNo);
        if (aggregate.getOrder() == null) {
            throw new ApiException("订单不存在");
        }
        if (!aggregate.getOrder().getUserId().equals(userId)) {
            throw new ApiException("无权查看此订单");
        }
        return R.success(orderConverter.aggregateToVO(aggregate));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/portal/cancel")
    public R<Boolean> portalCancel(@RequestParam String orderNo) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(orderService.cancel(orderNo, userId));
    }
}
