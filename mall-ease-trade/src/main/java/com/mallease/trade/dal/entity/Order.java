package com.mallease.trade.dal.entity;

import com.mallease.common.enums.OrderStockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 幂等请求ID，防止重复提交
     */
    private String requestId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 省
     */
    private String receiverProvince;

    /**
     * 市
     */
    private String receiverCity;

    /**
     * 区
     */
    private String receiverDistrict;

    /**
     * 详细地址
     */
    private String receiverAddress;

    /**
     * 商品总金额
     */
    private BigDecimal totalAmount;

    /**
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 应付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单支付截止时间
     */
    private LocalDateTime payExpireTime;

    /**
     * 订单主状态，详见 {@link OrderStatus}
     */
    private Integer status;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 库存处理状态，详见 {@link OrderStockStatus}
     *
     * @see OrderStockStatus
     */
    private Integer stockProcessStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
