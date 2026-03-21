package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashCurrentDTO {
    private Long sessionId;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer timeStatus;

    private Long serverTime;

    private List<FlashProduct> products;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlashProduct {

        private Long id;

        private Long spuId;

        private String spuName;

        private String spuPic;

        private BigDecimal compareAtPrice;

        private BigDecimal flashPrice;

        private Integer discountPercent;
    }
}
