package com.mallease.trade.model.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单物流发货实体（映射 trade_order_shipment 表）
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderShipment {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 物流公司名称
     */
    private String logisticsCompany;

    /**
     * 物流公司编码（快递100编码）
     */
    private String logisticsCode;

    /**
     * 物流运单号
     */
    private String logisticsNo;

    /**
     * 发货人
     */
    private String shipperName;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 签收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 物流状态：0-已发货 1-运输中 2-已签收
     */
    private Integer status;

    /**
     * 物流轨迹JSON
     */
    private String logisticsInfo;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
