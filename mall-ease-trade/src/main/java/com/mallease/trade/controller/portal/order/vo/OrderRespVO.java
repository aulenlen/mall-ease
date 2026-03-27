package com.mallease.trade.controller.portal.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单视图对象
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
@Schema(description = "订单信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRespVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "省")
    private String receiverProvince;

    @Schema(description = "市")
    private String receiverCity;

    @Schema(description = "区")
    private String receiverDistrict;

    @Schema(description = "详细地址")
    private String receiverAddress;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "运费")
    private BigDecimal freightAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    @Schema(description = "订单支付截止时间")
    private LocalDateTime payExpireTime;

    @Schema(description = "订单状态编码，详见 OrderStatus")
    private Integer status;

    @Schema(description = "订单状态描述")
    private String statusDesc;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "商品总件数")
    private Integer totalQuantity;

    @Schema(description = "订单商品列表")
    private List<OrderItemRespVO> items;
}
