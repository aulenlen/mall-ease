package com.mallease.marketing.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 秒杀商品视图对象
 *
 * @author: Aulen
 * @create: 2026-01-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "秒杀商品视图对象")
public class FlashProductVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "秒杀活动ID")
    private Long flashActivityId;

    @Schema(description = "秒杀场次ID")
    private Long flashSessionId;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SPU 名称")
    private String spuName;

    @Schema(description = "SPU 图片")
    private String spuPic;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SKU 图片")
    private String skuPic;

    @Schema(description = "SKU 规格属性")
    private String attrValues;

    @Schema(description = "市场价（原价）")
    private BigDecimal originalPrice;

    @Schema(description = "秒杀价格")
    private BigDecimal flashPrice;

    @Schema(description = "秒杀库存")
    private Integer flashStock;

    @Schema(description = "每人限购数量")
    private Integer flashLimit;

    @Schema(description = "排序")
    private Integer sort;
}