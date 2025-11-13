package com.mallease.pms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 产品满减请求参数
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductFullReductionRequest {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 满减价格
     */
    @NotNull(message = "满减价格不能为空")
    private BigDecimal fullPrice;

    /**
     * 减价
     */
    @NotNull(message = "减价不能为空")
    private BigDecimal reducePrice;
}
