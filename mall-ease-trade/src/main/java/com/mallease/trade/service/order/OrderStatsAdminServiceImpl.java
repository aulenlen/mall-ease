package com.mallease.trade.service.order;

import com.mallease.common.enums.OrderStatus;
import com.mallease.trade.controller.admin.order.vo.OrderStatsOverviewRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatusDistributionRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsTrendRespVO;
import com.mallease.trade.service.order.OrderStatsAdminService;
import com.mallease.trade.service.order.OrderService;
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
public class OrderStatsAdminServiceImpl implements OrderStatsAdminService {

    private final OrderService orderService;

    @Override
    public OrderStatsOverviewRespVO overview() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 今日统计
        Map<String, Object> todayStats = orderService.getTodayOrderOverview(todayStart);
        Long todayCount = toLong(todayStats.get("orderCount"));
        BigDecimal todayAmount = toBigDecimal(todayStats.get("orderAmount"));

        // 全量统计
        Map<String, Object> totalStats = orderService.getTotalOrderOverview();
        Long totalCount = toLong(totalStats.get("orderCount"));
        BigDecimal totalAmount = toBigDecimal(totalStats.get("orderAmount"));

        // 各待处理状态数量
        Long pendingPaymentCount = orderService.countOrdersByStatus(OrderStatus.PENDING_PAYMENT.getCode());
        Long pendingShipmentCount = orderService.countOrdersByStatus(OrderStatus.PAID.getCode())
                + orderService.countOrdersByStatus(OrderStatus.PENDING_SHIPMENT.getCode());
        Long pendingReceiptCount = orderService.countOrdersByStatus(OrderStatus.PENDING_RECEIPT.getCode());

        return OrderStatsOverviewRespVO.builder()
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
    public List<OrderStatsTrendRespVO> trend(Integer days) {
        int queryDays = days != null && days > 0 ? days : 7;
        LocalDateTime startDate = LocalDate.now().minusDays(queryDays - 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().atTime(LocalTime.MAX);
        return orderService.listOrderStatsTrend(startDate, endDate);
    }

    @Override
    public List<OrderStatusDistributionRespVO> statusDistribution() {
        List<OrderStatusDistributionRespVO> distributions = orderService.listOrderStatusDistribution();
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
