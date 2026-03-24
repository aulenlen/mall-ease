package com.mallease.product.controller.admin.stock.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存 SKU 库存请求
 */
@Schema(description = "保存 SKU 库存请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuStockSaveReqVO {

    public interface Create {
    }

    public interface Update {
    }

    @Schema(description = "主键 ID（更新时必填）")
    @NotNull(groups = Update.class, message = "更新时 ID 不能为空")
    private Long id;

    @Schema(description = "SKU ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时 SKU ID 不能为空")
    private Long skuId;

    @Schema(description = "SPU ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时 SPU ID 不能为空")
    private Long spuId;

    @Schema(description = "可用库存", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Schema(description = "库存预警值")
    @Min(value = 0, message = "库存预警值不能为负数")
    private Integer lowStock;

    @Schema(description = "库存状态: 0-无货, 1-有货, 2-预售")
    @Min(value = 0, message = "库存状态值必须为 0-2")
    @Max(value = 2, message = "库存状态值必须为 0-2")
    @Builder.Default
    private Integer stockStatus = 1;
}