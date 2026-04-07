package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashDeductResultDTO {
    private Boolean success;
    private Integer errorCode;
    private String errorMessage;
    private Long flashProductId;
    private Long spuId;
    private Long skuId;
    private BigDecimal flashPrice;
    private Integer flashLimit;
    private String spuName;
    private String skuPic;
    private String skuAttrs;
}
