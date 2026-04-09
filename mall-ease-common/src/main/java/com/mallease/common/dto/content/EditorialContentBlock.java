package com.mallease.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 编辑精选正文块。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "编辑精选正文块")
public class EditorialContentBlock {

    @Schema(description = "块ID")
    @NotBlank(message = "正文块ID不能为空")
    @Size(max = 64, message = "正文块ID长度不能超过64个字符")
    private String id;

    @Schema(description = "块类型")
    @NotNull(message = "正文块类型不能为空")
    private EditorialContentBlockType type;

    @Schema(description = "文本内容")
    @Size(max = 5000, message = "正文块内容长度不能超过5000个字符")
    private String content;

    @Schema(description = "图片URL")
    @Size(max = 1000, message = "图片URL长度不能超过1000个字符")
    private String url;

    @Schema(description = "图片替代文本")
    @Size(max = 255, message = "图片替代文本长度不能超过255个字符")
    private String alt;

    @Schema(description = "图片说明")
    @Size(max = 255, message = "图片说明长度不能超过255个字符")
    private String caption;
}
