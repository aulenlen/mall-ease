package com.mallease.product.controller.admin.sku.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 保存 SKU 请求
 */
@Schema(description = "保存 SKU 请求")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuSaveReqVO {

    public interface Create {
    }

    public interface Update {
    }

    @Schema(description = "SKU ID（有 ID 表示更新现有 SKU，无 ID 表示新增 SKU）")
    @NotNull(groups = Update.class, message = "更新时 SKU ID 不能为空")
    private Long id;

    @Schema(description = "SKU 规格值列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(groups = Create.class, message = "创建时 SKU 规格值不能为空")
    @Valid
    private List<AttrValueReqVO> attrValues;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKU 规格值")
    public static class AttrValueReqVO implements Serializable {

        @Schema(description = "属性 ID")
        @NotNull(message = "属性 ID 不能为空")
        private Long attrId;

        @Schema(description = "属性名称")
        private String attrName;

        @Schema(description = "属性值")
        @NotBlank(message = "属性值不能为空")
        private String attrValue;
    }

    @Schema(description = "SKU 图片 URL")
    @Size(max = 255, message = "图片 URL 长度不能超过 255 个字符")
    private String pic;

    @Schema(description = "SKU 基础成交价", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时 SKU 基础成交价不能为空")
    @DecimalMin(value = "0.01", message = "基础成交价必须大于 0")
    private BigDecimal basePrice;

    @Schema(description = "SKU 划线参考价")
    @DecimalMin(value = "0", message = "划线参考价不能为负数")
    private BigDecimal compareAtPrice;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "启用状态值必须为 0 或 1")
    @Max(value = 1, message = "启用状态值必须为 0 或 1")
    @Builder.Default
    private Integer enableStatus = 1;

    @Schema(description = "库存信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时库存信息不能为空")
    @Valid
    private SkuStockReqVO stock;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKU 库存信息")
    public static class SkuStockReqVO {

        @Schema(description = "可用库存", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "库存不能为空")
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
}