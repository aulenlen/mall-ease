package com.mallease.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 图片块。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "图片块")
public class ImageBlock extends ContentBlock {

    @Schema(description = "图片URL")
    @NotBlank(message = "图片URL不能为空")
    @Size(max = 1000, message = "图片URL长度不能超过1000个字符")
    private String url;

    @Schema(description = "替代文本")
    @Size(max = 255, message = "图片替代文本长度不能超过255个字符")
    private String alt;

    @Schema(description = "图片说明")
    @Size(max = 255, message = "图片说明长度不能超过255个字符")
    private String caption;
}
