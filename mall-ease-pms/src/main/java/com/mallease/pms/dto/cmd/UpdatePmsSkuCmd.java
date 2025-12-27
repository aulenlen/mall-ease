package com.mallease.pms.dto.cmd;

import com.mallease.pms.dto.SkuSpecValue;
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
 * @author: Aulen
 * @create: 2025-12-12
 */

@Schema(description = "更新SKU命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePmsSkuCmd {

    // SKU 标识（有ID=更新，无ID=新增）

    @Schema(description = "SKU ID（有ID表示更新现有SKU，无ID表示新增SKU）")
    private Long id;

    // SKU 基础信息（可选更新）

    @Schema(description = "SKU规格值列表")

    @Valid

    private List<SkuSpecValue> specValues;

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

    // 促销信息（可选更新）

    @Schema(description = "促销信息")
    @Valid
    private CreatePmsSkuCmd.SkuPromotionCmd promotion;

    // 价格策略（全量替换）

    @Schema(description = "会员价格列表（全量替换）")
    @Valid
    private List<CreatePmsSkuCmd.SkuMemberPriceCmd> memberPriceList;

    @Schema(description = "阶梯价格列表（全量替换）")
    @Valid
    private List<CreatePmsSkuCmd.SkuLadderCmd> ladderList;

    // 自定义 Getter（覆盖 Lombok 生成的方法，自动过滤无效数据）

    /**
     * 获取促销信息（自动过滤无效数据）
     */
    public CreatePmsSkuCmd.SkuPromotionCmd getPromotion() {
        if (promotion == null) {
            return null;
        }
        if (promotion.getPromotionPrice() == null || promotion.getPromotionPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return promotion;
    }

    /**
     * 获取会员价格列表（自动过滤无效数据）
     */
    public List<CreatePmsSkuCmd.SkuMemberPriceCmd> getMemberPriceList() {
        if (memberPriceList == null || memberPriceList.isEmpty()) {
            return null;
        }
        List<CreatePmsSkuCmd.SkuMemberPriceCmd> validList = memberPriceList.stream()
            .filter(CreatePmsSkuCmd.SkuMemberPriceCmd::isValid)
            .collect(java.util.stream.Collectors.toList());
        return validList.isEmpty() ? null : validList;
    }

    /**
     * 获取阶梯价格列表（自动过滤无效数据）
     */
    public List<CreatePmsSkuCmd.SkuLadderCmd> getLadderList() {
        if (ladderList == null || ladderList.isEmpty()) {
            return null;
        }
        List<CreatePmsSkuCmd.SkuLadderCmd> validList = ladderList.stream()
            .filter(CreatePmsSkuCmd.SkuLadderCmd::isValid)
            .collect(java.util.stream.Collectors.toList());
        return validList.isEmpty() ? null : validList;
    }
}
