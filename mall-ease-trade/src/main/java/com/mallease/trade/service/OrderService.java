package com.mallease.trade.service;

import com.mallease.common.enums.StockReleaseStatus;
import com.mallease.trade.model.aggregate.OrderAggregate;
import com.mallease.trade.model.client.vo.OrderConfirmVO;
import com.mallease.trade.model.data.entity.Order;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
public interface OrderService {

    /**
     * 生成结算快照
     */
    OrderConfirmVO generateSnapshot(Long userId, List<Long> cartItemIds);

    /**
     * 提交订单
     */
    String submit(Long userId, Order order, String requestId);

    /**
     * 创建订单（内部使用）
     */
    String create(OrderAggregate aggregate);

    /**
     * 根据订单编号查询
     */
    OrderAggregate getByOrderNo(String orderNo);

    /**
     * 查询用户订单列表
     */
    List<OrderAggregate> listByUserId(Long userId, Integer status);

    /**
     * 取消订单（用户主动取消）
     */
    boolean cancel(String orderNo, Long userId);

    /**
     * 批量取消订单（定时任务/内部调用）
     *
     * @param orderNos 订单编号列表
     */
    void cancelByOrderNos(List<String> orderNos);

    /**
     * 获取快照
     */
    OrderConfirmVO getSnapshot(String requestId);

    /**
     * 删除快照
     */
    void deleteSnapshot(String requestId);

    /**
     * 查询需要处理的订单（超时未支付 或 库存释放失败）
     */
    List<Order> listNeedProcess();

    Map<String, StockReleaseStatus> tryReleaseStock(List<String> orderNos);

    int orderReleaseSuccess(List<String> released);

    int orderReleaseFailed(List<String> failed);
}
