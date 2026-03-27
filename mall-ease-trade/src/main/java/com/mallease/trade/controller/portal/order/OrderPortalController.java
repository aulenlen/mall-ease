package com.mallease.trade.controller.portal.order;

import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.common.util.LoginContextUtil;
import com.mallease.trade.controller.portal.order.vo.OrderConfirmRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitReqVO;
import com.mallease.trade.controller.portal.order.vo.OrderPageRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitRespVO;
import com.mallease.trade.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 订单控制器
 */
@Tag(name = "订单管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trade/order")
public class OrderPortalController {

    private final OrderService orderService;

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
        Page<OrderRespVO> orderPage = orderService.pageUserOrders(userId, status, pageNum, pageSize);

        OrderPageRespVO result = OrderPageRespVO.builder()
                .pageNum(orderPage.getPageNum())
                .pageSize(orderPage.getPageSize())
                .total(orderPage.getTotal())
                .totalPage(orderPage.getTotalPage())
                .list(orderPage.getList())
                .serverTime(LocalDateTime.now())
                .build();
        return R.success(result);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/portal/detail")
    public R<OrderRespVO> portalDetail(@RequestParam String orderNo) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(orderService.getUserOrderDetail(userId, orderNo));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/portal/cancel")
    public R<Boolean> portalCancel(@RequestParam String orderNo,
                                   @RequestParam(defaultValue = "0") Integer restoreCart) {
        Long userId = LoginContextUtil.getUserId();
        return R.success(orderService.cancelOrder(orderNo, userId, restoreCart != null && restoreCart == 1));
    }
}
