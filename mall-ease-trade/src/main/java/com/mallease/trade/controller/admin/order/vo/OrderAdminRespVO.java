package com.mallease.trade.controller.admin.order.vo;

import com.mallease.trade.controller.admin.payment.vo.PaymentRespVO;
import com.mallease.trade.controller.portal.order.vo.OrderItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端订单视图对象
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Schema(description = "管理端订单信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAdminRespVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

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

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "订单状态描述")
    private String statusDesc;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "订单商品列表")
    private List<OrderItemRespVO> items;

    @Schema(description = "支付信息")
    private PaymentRespVO payment;

    @Schema(description = "物流信息")
    private OrderShipmentRespVO shipment;
}
