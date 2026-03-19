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
@NoArgsConstructor
@AllArgsConstructor
public class SpuFlashOverlayDTO {

    private Long sessionId;

    private Long spuId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<SkuFlashOverlayDTO> skuFlashList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkuFlashOverlayDTO {

        private Long skuId;

        private BigDecimal originalPrice;

        private BigDecimal flashPrice;

        private Integer flashStock;

        private Integer flashLimit;
    }
}
