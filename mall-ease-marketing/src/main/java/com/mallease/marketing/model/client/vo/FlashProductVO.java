package com.mallease.marketing.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Schema(description = "秒杀场次ID")
    private Long flashSessionId;

    @Schema(description = "秒杀场次名称")
    private String sessionName;

    @Schema(description = "场次开始时间")
    private LocalDateTime sessionStartTime;

    @Schema(description = "场次结束时间")
    private LocalDateTime sessionEndTime;

    @Schema(description = "场次状态：0-禁用 1-启用")
    private Integer sessionStatus;

    @Schema(description = "场次时间状态：0-未开始 1-进行中 2-已结束")
    private Integer timeStatus;

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

    @Schema(description = "路由类型：0-非热点秒杀 1-热点秒杀")
    private Integer routeType;

    @Schema(description = "排序")
    private Integer sort;
}
