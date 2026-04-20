package com.mallease.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 文章正文。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章正文文档")
public class ContentDocument {

    @Schema(description = "正文结构版本")
    @NotNull(message = "正文版本不能为空")
    @Min(value = 1, message = "正文版本不合法")
    @Max(value = 1, message = "正文版本不合法")
    private Integer version;

    @Schema(description = "正文块列表")
    @Valid
    @NotNull(message = "正文块列表不能为空")
    private List<ContentBlock> blocks;
}
