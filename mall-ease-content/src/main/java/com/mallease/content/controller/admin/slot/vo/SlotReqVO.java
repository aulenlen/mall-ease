package com.mallease.content.controller.admin.slot.vo;

import com.mallease.content.constant.ContentStatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "槽位设置参数")
public class SlotReqVO {

    @Schema(description = ContentStatusConstants.ENABLE_STATUS_SCHEMA, example = "1")
    @Min(value = ContentStatusConstants.ENABLE_STATUS_DISABLED, message = ContentStatusConstants.ENABLE_STATUS_INVALID_MESSAGE)
    @Max(value = ContentStatusConstants.ENABLE_STATUS_ENABLED, message = ContentStatusConstants.ENABLE_STATUS_INVALID_MESSAGE)
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String note;
}
