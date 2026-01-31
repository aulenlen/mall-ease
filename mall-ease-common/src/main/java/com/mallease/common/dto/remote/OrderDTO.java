package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据传输对象（跨服务传输）
 *
 * @author: Aulen
 * @create: 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID（创建时无，返回时有）
     */
    private Long id;

    /**
     * 订单编号（创建时无，返回时有）
     */
    private String orderNo;

    /**
     * 幂等请求ID（创建时必填）
     */
    private String requestId;

    /**
     * 用户ID（BFF 从 Token 获取后填充）
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
     * 订单状态: 1-待发货 2-待收货 3-已完成 4-已取消
     */
    private Integer status;

    /**
     * 订单状态描述
     */
    private String statusDesc;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单商品列表
     */
    private List<OrderItemDTO> items;

    /**
     * 订单商品项 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 订单商品ID
         */
        private Long id;

        /**
         * SPU ID
         */
        private Long spuId;

        /**
         * SKU ID
         */
        private Long skuId;

        /**
         * 商品名称
         */
        private String spuName;

        /**
         * SKU图片
         */
        private String skuPic;

        /**
         * SKU规格属性
         */
        private String skuAttrs;

        /**
         * 下单时单价
         */
        private BigDecimal price;

        /**
         * 购买数量
         */
        private Integer quantity;

        /**
         * 小计金额
         */
        private BigDecimal subtotal;
    }
}