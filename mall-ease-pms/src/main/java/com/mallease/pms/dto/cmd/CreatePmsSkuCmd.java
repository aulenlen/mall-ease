package com.mallease.pms.dto.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mallease.pms.dto.SkuSpecValue;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建SKU命令对象
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

    @Schema(description = "SKU规格值列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "SKU规格值不能为空")
    @Valid
    private List<SkuSpecValue> specValues;

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

    @Schema(description = "促销信息（可选）")
    private SkuPromotionCmd promotion;

    // ========================================================================
    // 价格策略（可选）
    // ========================================================================

    @Schema(description = "会员价格列表（可选）")
    private List<SkuMemberPriceCmd> memberPriceList;

    @Schema(description = "阶梯价格列表（可选）")
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
     * 说明：整个 memberPriceList 是可选的，但如果传入则需要有效数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKU会员价格")
    public static class SkuMemberPriceCmd {

        @Schema(description = "会员等级ID")
        private Long memberLevelId;

        @Schema(description = "会员等级名称")
        @Size(max = 100, message = "会员等级名称长度不能超过100个字符")
        private String memberLevelName;

        @Schema(description = "会员价格")
        @DecimalMin(value = "0.01", message = "会员价格必须大于0")
        private BigDecimal memberPrice;

        /**
         * 判断是否为有效的会员价格配置
         */
        public boolean isValid() {
            return memberLevelId != null && memberPrice != null && memberPrice.compareTo(BigDecimal.ZERO) > 0;
        }
    }

    /**
     * SKU阶梯价格命令（内部类）
     * 说明：整个 ladderList 是可选的，但如果传入则需要有效数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "SKU阶梯价格")
    public static class SkuLadderCmd {

        @Schema(description = "满足数量")
        @Min(value = 1, message = "满足数量必须大于0")
        private Integer count;

        @Schema(description = "折扣（0.00-1.00）")
        @DecimalMin(value = "0.01", message = "折扣必须大于0")
        @DecimalMax(value = "1.00", message = "折扣不能大于1")
        private BigDecimal discount;

        @Schema(description = "折后价格")
        @DecimalMin(value = "0", message = "折后价格不能为负数")
        private BigDecimal price;

        /**
         * 判断是否为有效的阶梯价格配置
         */
        public boolean isValid() {
            return count != null && count > 0 && discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
        }
    }

    // ========================================================================
    // 自定义 Getter（覆盖 Lombok 生成的方法，自动过滤无效数据）
    // ========================================================================

    /**
     * 获取促销信息（自动过滤无效数据）
     * <p>
     * 只有当促销价格有效（不为null且大于0）时才返回促销信息
     */
    public SkuPromotionCmd getPromotion() {
        if (promotion == null) {
            return null;
        }
        // 促销价格无效时，视为无促销信息
        if (promotion.getPromotionPrice() == null || promotion.getPromotionPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return promotion;
    }

    /**
     * 获取会员价格列表（自动过滤无效数据）
     * <p>
     * 过滤掉无效的会员价格配置，若过滤后为空则返回null
     */
    public List<SkuMemberPriceCmd> getMemberPriceList() {
        if (memberPriceList == null || memberPriceList.isEmpty()) {
            return null;
        }
        List<SkuMemberPriceCmd> validList = memberPriceList.stream().filter(SkuMemberPriceCmd::isValid).collect(Collectors.toList());
        return validList.isEmpty() ? null : validList;
    }

    /**
     * 获取阶梯价格列表（自动过滤无效数据）
     * <p>
     * 过滤掉无效的阶梯价格配置，若过滤后为空则返回null
     */
    public List<SkuLadderCmd> getLadderList() {
        if (ladderList == null || ladderList.isEmpty()) {
            return null;
        }
        List<SkuLadderCmd> validList = ladderList.stream().filter(SkuLadderCmd::isValid).collect(Collectors.toList());
        return validList.isEmpty() ? null : validList;
    }
}
