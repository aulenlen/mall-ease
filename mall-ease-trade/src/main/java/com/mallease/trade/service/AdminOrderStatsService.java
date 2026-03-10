package com.mallease.trade.service;

import com.mallease.trade.model.client.vo.OrderStatsOverviewVO;
import com.mallease.trade.model.client.vo.OrderStatusDistributionVO;
import com.mallease.trade.model.client.vo.OrderStatsTrendVO;

import java.util.List;

/**
 * 管理端订单统计服务接口
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
public interface AdminOrderStatsService {

    /**
     * 总览数据
     *
     * @return 统计总览
     */
    OrderStatsOverviewVO overview();

    /**
     * 趋势数据
     *
     * @param days 统计天数（默认7天）
     * @return 趋势数据
     */
    List<OrderStatsTrendVO> trend(Integer days);

    /**
     * 各状态订单分布
     *
     * @return 状态分布
     */
    List<OrderStatusDistributionVO> statusDistribution();
}
