package com.mallease.product.model.client.cmd;

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
 * 保存SKU命令（创建/更新统一）
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "保存SKU命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuCmd {

    /**
     * 创建时的校验组
     */
    public interface Create {
    }

    /**
     * 更新时的校验组
     */
    public interface Update {
    }

    @Schema(description = "SKU ID（有ID表示更新现有SKU，无ID表示新增SKU）")
    @NotNull(groups = Update.class, message = "更新时SKU ID不能为空")
    private Long id;

    @Schema(description = "SKU规格值列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(groups = Create.class, message = "创建时SKU规格值不能为空")
    @Valid
    private List<AttrValueCmd> attrValues;

    /**
     * 属性值命令（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "属性值")
    public static class AttrValueCmd implements Serializable {

        @Schema(description = "属性ID")
        @NotNull(message = "属性ID不能为空")
        private Long attrId;

        @Schema(description = "属性名称")
        private String attrName;

        @Schema(description = "属性值")
        @NotBlank(message = "属性值不能为空")
        private String attrValue;
    }

    @Schema(description = "SKU图片URL")
    @Size(max = 255, message = "图片URL长度不能超过255个字符")
    private String pic;

    @Schema(description = "SKU基础成交价", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时SKU基础成交价不能为空")
    @DecimalMin(value = "0.01", message = "基础成交价必须大于0")
    private BigDecimal basePrice;

    @Schema(description = "SKU划线参考价")
    @DecimalMin(value = "0", message = "划线参考价不能为负数")
    private BigDecimal compareAtPrice;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "启用状态值必须为0或1")
    @Max(value = 1, message = "启用状态值必须为0或1")
    @Builder.Default
    private Integer enableStatus = 1;

    @Schema(description = "库存信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "创建时库存信息不能为空")
    @Valid
    private SkuStockCmd stock;

    /**
     * SKU库存命令（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKU库存信息")
    public static class SkuStockCmd {

        @Schema(description = "可用库存", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "库存不能为空")
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

}
