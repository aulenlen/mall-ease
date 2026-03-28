package com.mallease.trade.controller.admin.order;

import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.trade.controller.admin.order.vo.OrderShipReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderUpdateReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderPageReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderAdminRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderShipmentRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsOverviewRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsTrendRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatusDistributionRespVO;
import com.mallease.trade.dal.entity.OrderOperationLog;
import com.mallease.trade.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端订单控制器
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Tag(name = "管理端-订单管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderAdminController {

    private final OrderService orderService;

    @Operation(summary = "订单分页列表", description = "支持多条件筛选")
    @GetMapping
    public R<Page<OrderAdminRespVO>> list(OrderPageReqVO reqVO) {
        return R.success(orderService.pageAdminOrders(reqVO));
    }

    @Operation(summary = "订单详情", description = "包含商品列表、支付信息和物流信息")
    @GetMapping("/{orderNo}")
    public R<OrderAdminRespVO> detail(@PathVariable String orderNo) {
        return R.success(orderService.getAdminOrderDetail(orderNo));
    }

    @Operation(summary = "发货", description = "填写物流公司和运单号，订单状态变更为待收货")
    @PostMapping("/shipments")
    public R<Void> ship(@Validated @RequestBody OrderShipReqVO reqVO) {
        orderService.shipOrder(reqVO);
        return R.success();
    }

    @Operation(summary = "查询物流信息")
    @GetMapping("/{orderNo}/shipment")
    public R<OrderShipmentRespVO> getShipment(@PathVariable String orderNo) {
        return R.success(orderService.getOrderShipment(orderNo));
    }

    @Operation(summary = "强制取消订单", description = "支持取消待支付/已支付/待收货订单，自动触发库存释放")
    @PostMapping("/{orderNo}/force-cancel")
    public R<Void> forceCancel(@PathVariable String orderNo) {
        orderService.forceCancelOrder(orderNo);
        return R.success();
    }

    @Operation(summary = "修改收货地址", description = "仅限待支付/已支付订单")
    @PutMapping("/address")
    public R<Void> updateAddress(@Validated @RequestBody OrderUpdateReqVO reqVO) {
        orderService.updateOrderAddress(reqVO);
        return R.success();
    }

    @Operation(summary = "修改备注")
    @PutMapping("/remark")
    public R<Void> updateRemark(@Validated @RequestBody OrderUpdateReqVO reqVO) {
        orderService.updateOrderRemark(reqVO);
        return R.success();
    }

    @Operation(summary = "调整金额", description = "仅限待支付订单")
    @PutMapping("/amount")
    public R<Void> adjustAmount(@Validated @RequestBody OrderUpdateReqVO reqVO) {
        orderService.adjustOrderAmount(reqVO);
        return R.success();
    }

    @Operation(summary = "查看操作日志")
    @GetMapping("/{orderNo}/operation-logs")
    public R<List<OrderOperationLog>> getOperationLogs(@PathVariable String orderNo) {
        return R.success(orderService.listOrderOperationLogs(orderNo));
    }

    @Operation(summary = "总览数据", description = "今日订单数/金额/各状态待处理数量")
    @GetMapping("/stats/overview")
    public R<OrderStatsOverviewRespVO> overview() {
        return R.success(orderService.getOrderStatsOverview());
    }

    @Operation(summary = "趋势数据", description = "按日统计订单数量和金额，默认7天")
    @GetMapping("/stats/trend")
    public R<List<OrderStatsTrendRespVO>> trend(@RequestParam(required = false, defaultValue = "7") Integer days) {
        return R.success(orderService.listAdminOrderStatsTrend(days));
    }

    @Operation(summary = "订单状态分布")
    @GetMapping("/stats/status-distribution")
    public R<List<OrderStatusDistributionRespVO>> statusDistribution() {
        return R.success(orderService.listAdminOrderStatusDistribution());
    }
}
