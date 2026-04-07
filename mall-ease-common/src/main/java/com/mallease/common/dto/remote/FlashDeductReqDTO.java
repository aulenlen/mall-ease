package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashDeductReqDTO {
    private Long sessionId;
    private Long skuId;
    private Long userId;
    private Integer quantity;
    private Integer flashLimit;
}
