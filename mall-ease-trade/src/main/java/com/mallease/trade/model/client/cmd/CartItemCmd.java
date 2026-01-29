package com.mallease.trade.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存购物车命令（添加/更新统一）
 * 使用 Validation Groups 区分添加和更新的校验规则：
 * - Create.class: 添加时的校验组
 * - Update.class: 更新时的校验组
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Schema(description = "保存购物车命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemCmd {

    /**
     * 添加时的校验组
     */
    public interface Create {
    }

    /**
     * 更新时的校验组
     */
    public interface Update {
    }

    @Schema(description = "购物车项ID（添加时不传，更新时必传）")
    @NotNull(groups = Update.class, message = "更新时购物车项ID不能为空")
    private Long id;

    @Schema(description = "SKU ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(groups = Create.class, message = "添加时SKU ID不能为空")
    private Long skuId;

    @Schema(description = "商品数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    @Builder.Default
    private Integer quantity = 1;

    @Schema(description = "选中状态: 0-未选中, 1-已选中")
    @Min(value = 0, message = "选中状态必须为0或1")
    @Max(value = 1, message = "选中状态必须为0或1")
    @Builder.Default
    private Integer checked = 1;
}