package com.mallease.trade.controller;

import com.mallease.common.api.R;
import com.mallease.trade.model.client.vo.OrderStatsOverviewVO;
import com.mallease.trade.model.client.vo.OrderStatusDistributionVO;
import com.mallease.trade.model.client.vo.OrderStatsTrendVO;
import com.mallease.trade.service.AdminOrderStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端订单统计控制器
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Tag(name = "管理端-订单统计")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trade/admin/stats")
public class AdminOrderStatsController {

    private final AdminOrderStatsService adminOrderStatsService;

    @Operation(summary = "总览数据", description = "今日订单数/金额/各状态待处理数量")
    @GetMapping("/overview")
    public R<OrderStatsOverviewVO> overview() {
        return R.success(adminOrderStatsService.overview());
    }

    @Operation(summary = "趋势数据", description = "按日统计订单数量和金额，默认7天")
    @GetMapping("/trend")
    public R<List<OrderStatsTrendVO>> trend(@RequestParam(required = false, defaultValue = "7") Integer days) {
        return R.success(adminOrderStatsService.trend(days));
    }

    @Operation(summary = "订单状态分布")
    @GetMapping("/statusDistribution")
    public R<List<OrderStatusDistributionVO>> statusDistribution() {
        return R.success(adminOrderStatsService.statusDistribution());
    }
}
