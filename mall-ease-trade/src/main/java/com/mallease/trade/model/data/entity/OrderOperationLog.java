package com.mallease.trade.model.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单操作日志实体（映射 trade_order_operation_log 表）
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderOperationLog {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作类型：1-发货 2-强制取消 3-修改地址 4-修改备注 5-调整金额 6-退款
     */
    private Integer operationType;

    /**
     * 操作详情
     */
    private String detail;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
