package com.mallease.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 标题块。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "标题块")
public class HeadingBlock extends ContentBlock {

    @Schema(description = "标题级别")
    @Min(value = 1, message = "标题级别最小为1")
    @Max(value = 6, message = "标题级别最大为6")
    private Integer level;

    @Schema(description = "标题内容")
    @NotBlank(message = "标题内容不能为空")
    @Size(max = 500, message = "标题内容长度不能超过500个字符")
    private String content;
}
