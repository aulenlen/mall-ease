package com.mallease.trade.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量更新购物车选中状态命令
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Schema(description = "批量更新购物车选中状态命令")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCheckedCmd {

    @Schema(description = "购物车项ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "购物车项ID列表不能为空")
    @Size(max = 100, message = "单次操作不能超过100条")
    private List<Long> ids;

    @Schema(description = "选中状态: 0-未选中, 1-已选中", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "选中状态不能为空")
    @Min(value = 0, message = "选中状态必须为0或1")
    @Max(value = 1, message = "选中状态必须为0或1")
    private Integer checked;
}