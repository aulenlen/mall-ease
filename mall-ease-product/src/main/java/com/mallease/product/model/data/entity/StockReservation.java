package com.mallease.product.model.data.entity;

import com.mallease.product.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 库存预占明细（按订单锁定/释放库存的核心表）
 *
 * @author: Aulen
 * @create: 2026-02-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservation {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号（幂等键，同一订单同一SKU只能有一条记录）
     */
    private String orderNo;

    /**
     * SPU ID（用于 Redis key 分组）
     */
    private Long spuId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 预占数量
     */
    private Integer quantity;

    /**
     * 预占状态：1-LOCKED 已锁定, 2-RELEASED 已释放, 3-CONFIRMED 已确认
     *
     * @see ReservationStatus
     */
    private Integer reservationStatus;

    /**
     * 过期时间（创建时间 + 30分钟）
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
