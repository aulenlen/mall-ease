package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashCreateOrderRespDTO {
    private String orderNo;
    private BigDecimal payAmount;
    private LocalDateTime payExpireTime;
    private Integer orderStatus;
    private LocalDateTime serverTime;
}
