package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 库存锁定请求 DTO
 *
 * @author: Aulen
 * @create: 2026-02-01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockLockDTO {
    /**
     * 幂等编号
     */
    private String requestId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 库存锁定信息
     * skuId → quantity
     */
    private Map<Long, Integer> lockStocks;
}
