package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 产品满减视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "产品满减")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductFullReductionVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "满减价格")
    private BigDecimal fullPrice;

    @Schema(description = "减价")
    private BigDecimal reducePrice;
}
