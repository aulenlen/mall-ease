package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品会员价格视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "商品会员价格")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsMemberPriceVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "会员等级ID")
    private Long memberLevelId;

    @Schema(description = "会员价格")
    private BigDecimal memberPrice;

    @Schema(description = "会员等级名称")
    private String memberLevelName;
}
