package com.mallease.trade.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发货命令对象
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Schema(description = "发货命令")
@Data
public class OrderShipReqVO {

    @Schema(description = "订单编号")
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @Schema(description = "物流公司名称")
    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;

    @Schema(description = "物流公司编码（快递100编码，选填）")
    private String logisticsCode;

    @Schema(description = "物流运单号")
    @NotBlank(message = "物流运单号不能为空")
    private String logisticsNo;
}
