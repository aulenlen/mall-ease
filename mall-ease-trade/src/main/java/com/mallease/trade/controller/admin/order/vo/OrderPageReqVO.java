package com.mallease.trade.controller.admin.order.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端订单查询对象
 *
 * @author: Aulen
 * @create: 2026-03-10
 */
@Schema(description = "订单查询条件")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderPageReqVO extends BaseQuery {

    @Schema(description = "订单编号（精确匹配）")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收货人手机号（模糊匹配）")
    private String receiverPhone;

    @Schema(description = "订单状态编码，详见 OrderStatus")
    private Integer status;

    @Schema(description = "支付渠道：1-支付宝 2-微信支付 9-模拟支付")
    private Integer payChannel;

    @Schema(description = "支付状态：1-待支付 2-支付成功 3-已关闭 4-支付失败")
    private Integer paymentStatus;

    @Schema(description = "创建时间-起始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间-结束")
    private LocalDateTime createTimeEnd;

    @Schema(description = "支付金额-最小值")
    private BigDecimal payAmountMin;

    @Schema(description = "支付金额-最大值")
    private BigDecimal payAmountMax;
}
