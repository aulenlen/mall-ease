package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存SKU库存命令（创建/更新统一）
 *
 * @author: Aulen
 * @create: 2026-01-05
 */
@Schema(description = "保存SKU库存命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuStockCmd {

    /**
     * 创建时的校验组
     */
    public interface Create {}

    /**
     * 更新时的校验组
     */
    public interface Update {}

    @Schema(description = "主键ID（更新时必填）")
    @NotNull(groups = Update.class, message = "更新时ID不能为空")
    private Long id;

    @Schema(description = "SKU ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时SKU ID不能为空")
    private Long skuId;

    @Schema(description = "SPU ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时SPU ID不能为空")
    private Long spuId;

    @Schema(description = "可用库存", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Schema(description = "库存预警值")
    @Min(value = 0, message = "库存预警值不能为负数")
    private Integer lowStock;

    @Schema(description = "库存状态: 0-无货, 1-有货, 2-预售")
    @Min(value = 0, message = "库存状态值必须为0-2")
    @Max(value = 2, message = "库存状态值必须为0-2")
    @Builder.Default
    private Integer stockStatus = 1;
}