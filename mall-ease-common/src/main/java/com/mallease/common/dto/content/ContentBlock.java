package com.mallease.common.dto.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 正文块基类。 */
@Data
@Schema(description = "正文块")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = HeadingBlock.class, name = "heading"),
        @JsonSubTypes.Type(value = ParagraphBlock.class, name = "paragraph"),
        @JsonSubTypes.Type(value = ImageBlock.class, name = "image"),
        @JsonSubTypes.Type(value = LeadBlock.class, name = "lead"),
        @JsonSubTypes.Type(value = SectionBlock.class, name = "section"),
        @JsonSubTypes.Type(value = ProductGroupBlock.class, name = "product_group")
})
public abstract class ContentBlock {

    @Schema(description = "块ID")
    @NotBlank(message = "正文块ID不能为空")
    @Size(max = 64, message = "正文块ID长度不能超过64个字符")
    private String id;
}
