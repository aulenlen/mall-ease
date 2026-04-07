package com.mallease.marketing.controller.portal.flash.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashOrderConfirmRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "幂等请求ID（提交订单时需带回）")
    private String requestId;

    @Schema(description = "商品项")
    private FlashOrderItemRespVO item;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "运费")
    private BigDecimal freightAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    // 内部字段

    private Long sessionId;

    private Long flashProductId;

    private Integer flashLimit;

    private Long userId;

    private LocalDateTime createTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlashOrderItemRespVO {

        @Schema(description = "SPU ID")
        private Long spuId;

        @Schema(description = "SKU ID")
        private Long skuId;

        @Schema(description = "商品名称")
        private String spuName;

        @Schema(description = "SKU图片")
        private String skuPic;

        @Schema(description = "SKU规格属性")
        private String skuAttrs;

        @Schema(description = "下单时单价")
        private BigDecimal price;

        @Schema(description = "购买数量")
        private Integer quantity;

        @Schema(description = "小计金额")
        private BigDecimal subtotal;
    }
}
