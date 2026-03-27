package com.mallease.trade.controller.admin.order;

import com.mallease.common.api.Page;
import com.mallease.common.api.R;
import com.mallease.trade.controller.admin.order.vo.OrderShipReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderUpdateReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderPageReqVO;
import com.mallease.trade.controller.admin.order.vo.OrderAdminRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderShipmentRespVO;
import com.mallease.trade.dal.entity.OrderOperationLog;
import com.mallease.trade.service.order.OrderAdminService;
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
@RequestMapping("/trade/admin/order")
public class OrderAdminController {

    private final OrderAdminService adminOrderService;

    @Operation(summary = "订单分页列表", description = "支持多条件筛选")
    @GetMapping("/list")
    public R<Page<OrderAdminRespVO>> list(OrderPageReqVO query) {
        return R.success(adminOrderService.list(query));
    }

    @Operation(summary = "订单详情", description = "包含商品列表、支付信息和物流信息")
    @GetMapping("/detail")
    public R<OrderAdminRespVO> detail(@RequestParam String orderNo) {
        return R.success(adminOrderService.detail(orderNo));
    }

    @Operation(summary = "发货", description = "填写物流公司和运单号，订单状态变更为待收货")
    @PostMapping("/ship")
    public R<Void> ship(@Validated @RequestBody OrderShipReqVO cmd) {
        adminOrderService.ship(cmd);
        return R.success();
    }

    @Operation(summary = "查询物流信息")
    @GetMapping("/shipment")
    public R<OrderShipmentRespVO> getShipment(@RequestParam String orderNo) {
        return R.success(adminOrderService.getShipment(orderNo));
    }

    @Operation(summary = "强制取消订单", description = "支持取消待支付/已支付/待收货订单，自动触发库存释放")
    @PostMapping("/forceCancel")
    public R<Void> forceCancel(@RequestParam String orderNo) {
        adminOrderService.forceCancel(orderNo);
        return R.success();
    }

    @Operation(summary = "修改收货地址", description = "仅限待支付/已支付订单")
    @PutMapping("/updateAddress")
    public R<Void> updateAddress(@Validated @RequestBody OrderUpdateReqVO cmd) {
        adminOrderService.updateAddress(cmd);
        return R.success();
    }

    @Operation(summary = "修改备注")
    @PutMapping("/updateRemark")
    public R<Void> updateRemark(@Validated @RequestBody OrderUpdateReqVO cmd) {
        adminOrderService.updateRemark(cmd);
        return R.success();
    }

    @Operation(summary = "调整金额", description = "仅限待支付订单")
    @PutMapping("/adjustAmount")
    public R<Void> adjustAmount(@Validated @RequestBody OrderUpdateReqVO cmd) {
        adminOrderService.adjustAmount(cmd);
        return R.success();
    }

    @Operation(summary = "查看操作日志")
    @GetMapping("/operationLogs")
    public R<List<OrderOperationLog>> getOperationLogs(@RequestParam String orderNo) {
        return R.success(adminOrderService.getOperationLogs(orderNo));
    }
}
