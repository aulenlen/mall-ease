package com.mallease.content.controller.admin.article.vo;

import com.mallease.common.constant.ContentEditorConstant;
import com.mallease.common.dto.content.ContentDocument;
import com.mallease.content.constant.ContentStatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章保存参数")
public class ArticleReqVO {

    public interface Create {}

    public interface Update {}

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "标题")
    @NotBlank(groups = {Create.class, Update.class}, message = "标题不能为空")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @Schema(description = "摘要")
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String subTitle;

    @Schema(description = "封面图URL")
    @Size(max = 500, message = "封面图URL长度不能超过500个字符")
    private String coverPic;

    @Schema(description = ContentEditorConstant.CONTENT_SCHEMA_DESCRIPTION)
    @Valid
    @NotNull(groups = {Create.class, Update.class}, message = "正文内容不能为空")
    private ContentDocument content;

    @Schema(description = ContentEditorConstant.EDITOR_SCHEMA_VERSION_DESCRIPTION)
    @Min(value = ContentEditorConstant.EDITOR_SCHEMA_VERSION_V1, message = ContentEditorConstant.EDITOR_SCHEMA_VERSION_INVALID_MESSAGE)
    @Max(value = ContentEditorConstant.EDITOR_SCHEMA_VERSION_V1, message = ContentEditorConstant.EDITOR_SCHEMA_VERSION_INVALID_MESSAGE)
    private Integer editorSchemaVersion;

    @Schema(description = "分类标签")
    @Size(max = 100, message = "分类标签长度不能超过100个字符")
    private String categoryLabel;

    @Schema(description = "作者")
    @Size(max = 64, message = "作者长度不能超过64个字符")
    private String author;

    @Schema(description = "排序值")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = ContentStatusConstants.ARTICLE_STATUS_SCHEMA)
    @Min(value = ContentStatusConstants.ARTICLE_STATUS_DRAFT, message = ContentStatusConstants.ARTICLE_STATUS_INVALID_MESSAGE)
    @Max(value = ContentStatusConstants.ARTICLE_STATUS_OFFLINE, message = ContentStatusConstants.ARTICLE_STATUS_INVALID_MESSAGE)
    private Integer status;
}
