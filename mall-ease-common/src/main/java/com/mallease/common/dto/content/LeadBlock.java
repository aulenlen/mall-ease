package com.mallease.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 导语块。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "导语块")
public class LeadBlock extends ContentBlock {

    @Schema(description = "导语内容")
    @NotBlank(message = "导语内容不能为空")
    @Size(max = 1000, message = "导语内容长度不能超过1000个字符")
    private String content;
}
