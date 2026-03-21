package com.mallease.bff.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 首页秒杀 VO
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "首页秒杀数据")
public class HomeFlashVO {

    @Schema(description = "场次ID")
    private Long sessionId;

    @Schema(description = "场次名称")
    private String name;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "时间状态：0-未开始 1-进行中 2-已结束")
    private Integer timeStatus;

    @Schema(description = "服务器时间戳")
    private Long serverTime;

    @Schema(description = "秒杀商品列表")
    private List<FlashProductVO> products;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "秒杀商品")
    public static class FlashProductVO {

        @Schema(description = "秒杀商品ID")
        private Long id;

        @Schema(description = "SPU ID")
        private Long spuId;

        @Schema(description = "商品名称")
        private String spuName;

        @Schema(description = "商品图片")
        private String spuPic;

        @Schema(description = "划线参考价")
        private BigDecimal compareAtPrice;

        @Schema(description = "秒杀价")
        private BigDecimal flashPrice;

        @Schema(description = "折扣百分比")
        private Integer discountPercent;
    }
}
