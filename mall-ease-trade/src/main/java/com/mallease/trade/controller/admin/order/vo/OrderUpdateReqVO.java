package com.mallease.trade.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 修改订单命令对象（地址/备注/金额）
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Schema(description = "修改订单命令")
@Data
public class OrderUpdateReqVO {

    @Schema(description = "订单编号")
    @NotBlank(message = "订单编号不能为空")
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

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "调整后的应付金额")
    private BigDecimal payAmount;
}
