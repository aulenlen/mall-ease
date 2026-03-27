package com.mallease.common.dto.remote;

import com.mallease.common.enums.ReservationAggregateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单级库存预占回查结果 DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationStatusDTO {

    /**
     * 聚合状态。
     */
    private ReservationAggregateStatus status;

    /**
     * 预占记录总数。
     */
    private Integer totalCount;

    /**
     * LOCKED 数量。
     */
    private Integer lockedCount;

    /**
     * CONFIRMED 数量。
     */
    private Integer confirmedCount;

    /**
     * RELEASED 数量。
     */
    private Integer releasedCount;
}
