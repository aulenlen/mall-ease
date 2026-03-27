package com.mallease.marketing.controller.admin.flash.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 秒杀商品保存命令
 *
 * @author: Aulen
 * @create: 2026-01-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashProductReqVO {

    public interface Create {}

    public interface Update {}

    @Schema(description = "主键ID（更新时必传）")
    @NotNull(groups = Update.class, message = "更新时主键ID不能为空")
    private Long id;

    @Schema(description = "秒杀场次ID")
    @NotNull(groups = Create.class, message = "秒杀场次ID不能为空")
    private Long flashSessionId;

    @Schema(description = "SPU ID")
    @NotNull(groups = Create.class, message = "SPU ID不能为空")
    private Long spuId;

    @Schema(description = "SKU ID")
    @NotNull(groups = Create.class, message = "SKU ID不能为空")
    private Long skuId;

    @Schema(description = "秒杀价格")
    @NotNull(groups = Create.class, message = "秒杀价格不能为空")
    @DecimalMin(value = "0.01", message = "秒杀价格必须大于0")
    private BigDecimal flashPrice;

    @Schema(description = "秒杀库存")
    @NotNull(groups = Create.class, message = "秒杀库存不能为空")
    @Min(value = 1, message = "秒杀库存必须大于0")
    private Integer flashStock;

    @Schema(description = "每人限购数量")
    @Min(value = 1, message = "限购数量必须大于0")
    private Integer flashLimit = 1;

    @Schema(description = "路由类型：0-非热点秒杀 1-热点秒杀")
    private Integer routeType = 0;

    @Schema(description = "排序（越小越靠前）")
    @Min(value = 0, message = "排序值不能为负数")
    private Integer sort = 0;
}
