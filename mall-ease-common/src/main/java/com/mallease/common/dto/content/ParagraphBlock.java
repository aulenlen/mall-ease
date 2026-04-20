package com.mallease.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 段落块。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "段落块")
public class ParagraphBlock extends ContentBlock {

    @Schema(description = "段落内容")
    @NotBlank(message = "段落内容不能为空")
    @Size(max = 5000, message = "段落内容长度不能超过5000个字符")
    private String content;
}
