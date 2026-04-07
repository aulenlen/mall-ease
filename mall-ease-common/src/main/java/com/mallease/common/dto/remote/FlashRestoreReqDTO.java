package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashRestoreReqDTO {
    private Long sessionId;
    private Long skuId;
    private Long userId;
    private Integer quantity;
}
