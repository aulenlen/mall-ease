package com.mallease.trade.controller.portal.order;

import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.exception.ApiException;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.controller.portal.order.vo.OrderConfirmRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitReqVO;
import com.mallease.trade.convert.order.OrderConvert;
import com.mallease.trade.service.order.model.OrderAggregate;
import com.mallease.trade.controller.portal.order.vo.OrderPageRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitRespVO;
import com.mallease.trade.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单控制器
 */
@Tag(name = "订单管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trade/order")
public class OrderPortalController {

    private final OrderService orderService;
    private final OrderConvert orderConvert;

    @Operation(summary = "订单确认页", description = "根据当前选中的购物车商品生成确认页快照")
    @GetMapping("/portal/confirm")
    public R<OrderConfirmRespVO> confirm() {
        Long userId = LoginContextUtil.getUserId();
        return R.success(orderService.createOrderSnapshot(userId));
    }

    @Operation(summary = "提交订单")
    @PostMapping("/portal/submit")
    public R<OrderSubmitRespVO> submit(@Validated @RequestBody OrderSubmitReqVO reqVO) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(orderService.submitOrder(userId, reqVO));
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/portal/list")
    public R<OrderPageRespVO> portalList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = LoginContextUtil.getUserId();
        Page<OrderAggregate> aggregatePage = orderService.pageUserOrders(userId, status, pageNum, pageSize);
        List<OrderRespVO> voList = orderConvert.aggregatesToVOs(aggregatePage.getList());

        OrderPageRespVO result = OrderPageRespVO.builder()
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
    public R<OrderRespVO> portalDetail(@RequestParam String orderNo) {
        Long userId = LoginContextUtil.getUserId();
        OrderAggregate aggregate = orderService.getOrderAggregate(orderNo);
        if (aggregate.getOrder() == null) {
            throw new ApiException("订单不存在");
        }
        if (!aggregate.getOrder().getUserId().equals(userId)) {
            throw new ApiException("无权查看此订单");
        }
        return R.success(orderConvert.aggregateToVO(aggregate));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/portal/cancel")
    public R<Boolean> portalCancel(@RequestParam String orderNo,
                                   @RequestParam(defaultValue = "0") Integer restoreCart) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(orderService.cancelOrder(orderNo, userId, restoreCart != null && restoreCart == 1));
    }
}
