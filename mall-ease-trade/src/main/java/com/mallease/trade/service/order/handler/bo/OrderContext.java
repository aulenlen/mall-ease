package com.mallease.trade.service.order.handler.bo;

import com.mallease.common.enums.OrderSource;
import com.mallease.trade.controller.portal.order.vo.OrderSubmitReqVO;
import com.mallease.trade.dal.entity.Order;
import com.mallease.trade.dal.entity.OrderItem;
import com.mallease.trade.service.order.handler.enums.OrderEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderContext {
    private Long userId;
    private String requestId;
    private OrderSource orderSource;

    private String orderNo;
    private LocalDateTime payExpireTime;

    private OrderSubmitReqVO receiver;
    private String remark;

    private BigDecimal totalAmount;
    private BigDecimal freightAmount;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;

    private Object sourceSnapshot;

    private Long flashSessionId;
    private Long flashProductId;

    private Object resourceReservation;

    private boolean orderPersisted;

    private Order order;
    private List<OrderItem> orderItems;

    private OrderEvent event;
}
