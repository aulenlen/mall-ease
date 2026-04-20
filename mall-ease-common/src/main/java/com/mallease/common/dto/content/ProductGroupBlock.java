package com.mallease.common.dto.content;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/** 商品组块。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品组块")
public class ProductGroupBlock extends ContentBlock {

    @Schema(description = "商品组标题")
    @Size(max = 255, message = "商品组标题长度不能超过255个字符")
    private String title;

    @Schema(description = "商品ID列表")
    @NotEmpty(message = "商品组商品ID列表不能为空")
    private List<Long> spuIds;

    @Schema(description = "商品组布局")
    @NotNull(message = "商品组布局不能为空")
    private ProductGroupLayout layout;
}
