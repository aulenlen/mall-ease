package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 产品阶梯价格视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "产品阶梯价格")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductLadderVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "满足的商品数量")
    private Integer count;

    @Schema(description = "折扣")
    private BigDecimal discount;

    @Schema(description = "折后价格")
    private BigDecimal price;
}
