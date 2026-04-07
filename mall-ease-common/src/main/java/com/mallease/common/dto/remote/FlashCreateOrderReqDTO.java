package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashCreateOrderReqDTO {
    private Long userId;
    private String requestId;
    private Long sessionId;
    private Long flashProductId;
    private Long spuId;
    private Long skuId;
    private Integer quantity;
    private BigDecimal flashPrice;
    private String spuName;
    private String skuPic;
    private String skuAttrs;

    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverAddress;
    private String remark;
}
