package com.mallease.trade.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 物流发货视图对象
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Schema(description = "物流发货信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderShipmentRespVO {

    @Schema(description = "物流公司名称")
    private String logisticsCompany;

    @Schema(description = "物流公司编码")
    private String logisticsCode;

    @Schema(description = "物流运单号")
    private String logisticsNo;

    @Schema(description = "发货人")
    private String shipperName;

    @Schema(description = "发货时间")
    private LocalDateTime shipTime;

    @Schema(description = "签收时间")
    private LocalDateTime receiveTime;

    @Schema(description = "物流状态：0-已发货 1-运输中 2-已签收")
    private Integer status;

    @Schema(description = "物流轨迹JSON")
    private String logisticsInfo;
}
