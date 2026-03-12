package com.mallease.trade.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表分页响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListPageVO {

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页条数")
    private Integer pageSize;

    @Schema(description = "总页数")
    private Integer totalPage;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "订单列表")
    private List<OrderVO> list;

    @Schema(description = "服务端当前时间")
    private LocalDateTime serverTime;
}
