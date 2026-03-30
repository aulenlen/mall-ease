package com.mallease.trade.controller.portal.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单确认页 VO（同时作为 Redis 快照缓存）
 *
 * @author: Aulen
 * @create: 2026-01-30
 */
@Schema(description = "订单确认页数据")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "幂等请求ID（提交订单时需带回）")
    private String requestId;

    @Schema(description = "商品列表")
    private List<OrderItemRespVO> items;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "运费")
    private BigDecimal freightAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    // 内部字段

//    @JsonIgnore
    private Long userId;

//    @JsonIgnore
    private LocalDateTime createTime;
}
