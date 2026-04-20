package com.mallease.content.controller.admin.slot.vo;

import com.mallease.content.constant.ContentStatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量更新状态参数")
public class StatusBatchReqVO {

    @Schema(description = "ID列表")
    @NotEmpty(message = "ID列表不能为空")
    private List<@NotNull(message = "ID不能为空") Long> ids;

    @Schema(description = ContentStatusConstants.ENABLE_STATUS_SCHEMA, example = "1")
    @NotNull(message = "状态不能为空")
    @Min(value = ContentStatusConstants.ENABLE_STATUS_DISABLED, message = ContentStatusConstants.ENABLE_STATUS_INVALID_MESSAGE)
    @Max(value = ContentStatusConstants.ENABLE_STATUS_ENABLED, message = ContentStatusConstants.ENABLE_STATUS_INVALID_MESSAGE)
    private Integer status;
}
