package com.mallease.trade.controller;

import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.converter.OrderConverter;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.client.cmd.SubmitOrderCmd;
import com.mallease.trade.model.client.vo.OrderConfirmVO;
import com.mallease.trade.model.client.vo.OrderListPageVO;
import com.mallease.trade.model.client.vo.OrderVO;
import com.mallease.trade.model.client.vo.SubmitOrderVO;
import com.mallease.trade.model.data.entity.Order;
import com.mallease.trade.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单控制器
 */
@Tag(name = "订单管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trade/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderConverter orderConverter;

    @Operation(summary = "订单确认页", description = "根据当前选中的购物车商品生成确认页快照")
    @GetMapping("/portal/confirm")
    public R<OrderConfirmVO> confirm() {
        return R.success(orderService.generateSnapshot());
    }

    @Operation(summary = "提交订单")
    @PostMapping("/portal/submit")
    public R<SubmitOrderVO> submit(@Validated @RequestBody SubmitOrderCmd cmd) {
        Long userId = LoginContextUtil.getUserId();
        Order order = orderConverter.cmdToEntity(cmd);
        return R.success(orderService.submit(userId, order, cmd.getRequestId()));
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/portal/list")
    public R<OrderListPageVO> portalList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = LoginContextUtil.getUserId();
        Page<OrderAggregate> aggregatePage = orderService.listByUserId(userId, status, pageNum, pageSize);
        List<OrderVO> voList = orderConverter.aggregatesToVOs(aggregatePage.getList());

        OrderListPageVO result = OrderListPageVO.builder()
                .pageNum(aggregatePage.getPageNum())
                .pageSize(aggregatePage.getPageSize())
                .total(aggregatePage.getTotal())
                .totalPage(aggregatePage.getTotalPage())
                .list(voList)
                .serverTime(LocalDateTime.now())
                .build();
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
    public R<Boolean> portalCancel(@RequestParam String orderNo,
                                   @RequestParam(defaultValue = "0") Integer restoreCart) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(orderService.cancel(orderNo, userId, restoreCart != null && restoreCart == 1));
    }
}
