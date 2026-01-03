package com.mallease.product.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SPU上下架命令对象
 *
 * @author: Aulen
 * @create: 2025-12-22
 */
@Schema(description = "SPU上下架命令")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishSpuCmd {

    @Schema(description = "SPU ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "SPU ID列表不能为空")
    @Size(max=100)
    private List<Long> spuIds;

    @Schema(description = "上架状态(0:下架 1:上架)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "上架状态不能为空")
    private Integer publishStatus;
}
