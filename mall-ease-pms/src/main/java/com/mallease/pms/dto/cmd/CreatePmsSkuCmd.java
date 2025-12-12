package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建SKU命令对象
 * <p>
 * 包含SKU基础信息、库存、促销、会员价、阶梯价等复合数据。
 * 可作为CreatePmsSpuCmd的子命令，也可独立使用。
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Schema(description = "创建SKU命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePmsSkuCmd {

    // ========================================================================
    // SKU 基础信息
    // ========================================================================

    @Schema(description = "SKU规格值（JSON格式，如：{\"颜色\":\"红色\",\"尺码\":\"XL\"}）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SKU规格值不能为空")
    private String specValues;

    @Schema(description = "SKU图片URL")
    @Size(max = 255, message = "图片URL长度不能超过255个字符")
    private String pic;

    @Schema(description = "SKU价格", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SKU价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @Schema(description = "市场价（划线价）")
    @DecimalMin(value = "0", message = "市场价不能为负数")
    private BigDecimal originalPrice;

    @Schema(description = "启用状态: 0-禁用, 1-启用")
    @Min(value = 0, message = "启用状态值必须为0或1")
    @Max(value = 1, message = "启用状态值必须为0或1")
    @Builder.Default
    private Integer enableStatus = 1;

    // ========================================================================
    // 库存信息
    // ========================================================================

    @Schema(description = "库存信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "库存信息不能为空")
    @Valid
    private SkuStockCmd stock;

    // ========================================================================
    // 促销信息（可选）
    // ========================================================================

    @Schema(description = "促销信息")
    @Valid
    private SkuPromotionCmd promotion;

    // ========================================================================
    // 价格策略（可选）
    // ========================================================================

    @Schema(description = "会员价格列表")
    @Valid
    private List<SkuMemberPriceCmd> memberPriceList;

    @Schema(description = "阶梯价格列表")
    @Valid
    private List<SkuLadderCmd> ladderList;

    // ========================================================================
    // 嵌套命令对象
    // ========================================================================

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

    /**
     * SKU促销命令（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKU促销信息")
    public static class SkuPromotionCmd {

        @Schema(description = "促销类型: 0-无促销, 1-促销价, 2-会员价, 3-阶梯价, 4-满减价, 5-限时购")
        @Min(value = 0, message = "促销类型值必须为0-5")
        @Max(value = 5, message = "促销类型值必须为0-5")
        @Builder.Default
        private Integer promotionType = 0;

        @Schema(description = "促销价格")
        @DecimalMin(value = "0", message = "促销价格不能为负数")
        private BigDecimal promotionPrice;

        @Schema(description = "促销开始时间")
        private LocalDateTime promotionStartTime;

        @Schema(description = "促销结束时间")
        private LocalDateTime promotionEndTime;

        @Schema(description = "活动限购数量（0表示不限购）")
        @Min(value = 0, message = "限购数量不能为负数")
        @Builder.Default
        private Integer promotionPerLimit = 0;

        @Schema(description = "赠送成长值")
        @Min(value = 0, message = "成长值不能为负数")
        @Builder.Default
        private Integer giftGrowth = 0;

        @Schema(description = "赠送积分")
        @Min(value = 0, message = "积分不能为负数")
        @Builder.Default
        private Integer giftPoint = 0;

        @Schema(description = "积分使用上限")
        @Min(value = 0, message = "积分使用上限不能为负数")
        private Integer usePointLimit;
    }

    /**
     * SKU会员价格命令（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKU会员价格")
    public static class SkuMemberPriceCmd {

        @Schema(description = "会员等级ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "会员等级ID不能为空")
        private Long memberLevelId;

        @Schema(description = "会员等级名称")
        @Size(max = 100, message = "会员等级名称长度不能超过100个字符")
        private String memberLevelName;

        @Schema(description = "会员价格", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "会员价格不能为空")
        @DecimalMin(value = "0.01", message = "会员价格必须大于0")
        private BigDecimal memberPrice;
    }

    /**
     * SKU阶梯价格命令（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKU阶梯价格")
    public static class SkuLadderCmd {

        @Schema(description = "满足数量", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "满足数量不能为空")
        @Min(value = 1, message = "满足数量必须大于0")
        private Integer count;

        @Schema(description = "折扣（0.00-1.00）", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "折扣不能为空")
        @DecimalMin(value = "0.01", message = "折扣必须大于0")
        @DecimalMax(value = "1.00", message = "折扣不能大于1")
        private BigDecimal discount;

        @Schema(description = "折后价格")
        @DecimalMin(value = "0", message = "折后价格不能为负数")
        private BigDecimal price;
    }
}
