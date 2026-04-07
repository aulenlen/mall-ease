package com.mallease.marketing.service.flash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashWarmUpResult {
    private String triggerType;
    private List<Long> sessionIds;
    private Integer sessionCount;
    private Integer spuCount;
    private Integer skuCount;
    private Long costMs;
    private String message;
}