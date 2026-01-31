package com.mallease.bff.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交易服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@FeignClient(name = "mall-ease-trade")
public interface TradeFeignClient {

    /**
     * 创建订单
     *
     * @param orderDTO 订单数据
     * @return 订单编号
     */
    @PostMapping("/trade/order/internal/create")
    R<String> createOrder(@RequestBody OrderDTO orderDTO);

    /**
     * 查询订单详情
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    @GetMapping("/trade/order/internal/detail")
    R<OrderDTO> getOrderByOrderNo(@RequestParam("orderNo") String orderNo);

    /**
     * 查询用户订单列表
     *
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    @GetMapping("/trade/order/internal/list")
    R<List<OrderDTO>> listOrdersByUserId(@RequestParam("userId") Long userId,
                                         @RequestParam(value = "status", required = false) Integer status);

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @param userId  用户ID
     * @return 是否成功
     */
    @PostMapping("/trade/order/internal/cancel")
    R<Boolean> cancelOrder(@RequestParam("orderNo") String orderNo,
                           @RequestParam("userId") Long userId);
}
