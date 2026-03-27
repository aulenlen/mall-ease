package com.mallease.trade.service.order;

import com.mallease.trade.controller.admin.order.vo.OrderStatsOverviewRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatusDistributionRespVO;
import com.mallease.trade.controller.admin.order.vo.OrderStatsTrendRespVO;

import java.util.List;

/**
 * 管理端订单统计服务接口
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
public interface OrderStatsAdminService {

    /**
     * 总览数据
     *
     * @return 统计总览
     */
    OrderStatsOverviewRespVO overview();

    /**
     * 趋势数据
     *
     * @param days 统计天数（默认7天）
     * @return 趋势数据
     */
    List<OrderStatsTrendRespVO> trend(Integer days);

    /**
     * 各状态订单分布
     *
     * @return 状态分布
     */
    List<OrderStatusDistributionRespVO> statusDistribution();
}
