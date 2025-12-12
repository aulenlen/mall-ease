package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 更新SKU命令对象
 * <p>
 * 支持部分更新，只更新传入的非空字段。
 * 注意：库存更新应通过专用的库存接口，不在此命令中处理。
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "更新SKU命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePmsSkuCmd {

    // ========================================================================
    // 必填字段
    // ========================================================================

    @Schema(description = "SKU ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SKU ID不能为空")
    private Long id;

    // ========================================================================
    // SKU 基础信息（可选更新）
    // ========================================================================

    @Schema(description = "SKU规格值（JSON格式）")
    private String specValues;

    @Schema(description = "SKU图片URL")
    @Size(max = 255, message = "图片URL长度不能超过255个字符")
    private String pic;

    @Schema(description = "SKU价格")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @Schema(description = "市场价（划线价）")
    @DecimalMin(value = "0", message = "市场价不能为负数")
    private BigDecimal originalPrice;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "启用状态值必须为0或1")
    @Max(value = 1, message = "启用状态值必须为0或1")
    private Integer enableStatus;

    // ========================================================================
    // 促销信息（可选更新）
    // ========================================================================

    @Schema(description = "促销信息")
    @Valid
    private CreatePmsSkuCmd.SkuPromotionCmd promotion;

    // ========================================================================
    // 价格策略（全量替换）
    // ========================================================================

    @Schema(description = "会员价格列表（全量替换）")
    @Valid
    private List<CreatePmsSkuCmd.SkuMemberPriceCmd> memberPriceList;

    @Schema(description = "阶梯价格列表（全量替换）")
    @Valid
    private List<CreatePmsSkuCmd.SkuLadderCmd> ladderList;
}
