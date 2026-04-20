package com.mallease.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 分段块。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "分段块")
public class SectionBlock extends ContentBlock {

    @Schema(description = "锚点")
    @NotBlank(message = "分段锚点不能为空")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "分段锚点格式不合法")
    @Size(max = 64, message = "分段锚点长度不能超过64个字符")
    private String anchor;

    @Schema(description = "分段标题")
    @NotBlank(message = "分段标题不能为空")
    @Size(max = 255, message = "分段标题长度不能超过255个字符")
    private String title;
}
