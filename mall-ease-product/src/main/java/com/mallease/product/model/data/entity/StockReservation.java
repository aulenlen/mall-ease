package com.mallease.product.model.data.entity;

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
    private Integer status;

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

    /**
     * 预占状态枚举
     */
    public enum ReservationStatus {
        /**
         * 已锁定（下单成功，等待支付）
         */
        LOCKED(1, "已锁定"),

        /**
         * 已释放（取消订单/支付超时）
         */
        RELEASED(2, "已释放"),

        /**
         * 已确认（支付成功，库存实扣）
         */
        CONFIRMED(3, "已确认");

        private final int code;
        private final String desc;

        ReservationStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
