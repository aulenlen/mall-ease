package com.mallease.trade.service.impl;

import com.mallease.common.enums.OrderStatus;
import com.mallease.trade.model.client.vo.OrderStatsOverviewVO;
import com.mallease.trade.model.client.vo.OrderStatusDistributionVO;
import com.mallease.trade.model.client.vo.OrderStatsTrendVO;
import com.mallease.trade.service.AdminOrderStatsService;
import com.mallease.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 管理端订单统计服务实现
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderStatsServiceImpl implements AdminOrderStatsService {

    private final OrderService orderService;

    @Override
    public OrderStatsOverviewVO overview() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 今日统计
        Map<String, Object> todayStats = orderService.statsTodayOverview(todayStart);
        Long todayCount = toLong(todayStats.get("orderCount"));
        BigDecimal todayAmount = toBigDecimal(todayStats.get("orderAmount"));

        // 全量统计
        Map<String, Object> totalStats = orderService.statsTotalOverview();
        Long totalCount = toLong(totalStats.get("orderCount"));
        BigDecimal totalAmount = toBigDecimal(totalStats.get("orderAmount"));

        // 各待处理状态数量
        Long pendingPaymentCount = orderService.countByStatus(OrderStatus.PENDING_PAYMENT.getCode());
        Long pendingShipmentCount = orderService.countByStatus(OrderStatus.PAID.getCode())
                + orderService.countByStatus(OrderStatus.PENDING_SHIPMENT.getCode());
        Long pendingReceiptCount = orderService.countByStatus(OrderStatus.PENDING_RECEIPT.getCode());

        return OrderStatsOverviewVO.builder()
                .todayOrderCount(todayCount)
                .todayOrderAmount(todayAmount)
                .pendingPaymentCount(pendingPaymentCount)
                .pendingShipmentCount(pendingShipmentCount)
                .pendingReceiptCount(pendingReceiptCount)
                .totalOrderCount(totalCount)
                .totalOrderAmount(totalAmount)
                .build();
    }

    @Override
    public List<OrderStatsTrendVO> trend(Integer days) {
        int queryDays = days != null && days > 0 ? days : 7;
        LocalDateTime startDate = LocalDate.now().minusDays(queryDays - 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().atTime(LocalTime.MAX);
        return orderService.statsTrend(startDate, endDate);
    }

    @Override
    public List<OrderStatusDistributionVO> statusDistribution() {
        List<OrderStatusDistributionVO> distributions = orderService.statsStatusDistribution();
        // 填充状态描述
        distributions.forEach(d ->
                d.setStatusDesc(OrderStatus.getDescriptionByCode(d.getStatus()))
        );
        return distributions;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        return ((Number) value).longValue();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(value.toString());
    }
}
