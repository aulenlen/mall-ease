package com.mallease.content.controller.admin.article.vo;

import com.mallease.common.constant.ContentEditorConstant;
import com.mallease.common.dto.content.ContentDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章响应")
public class ArticleRespVO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String subTitle;

    @Schema(description = "封面图URL")
    private String coverPic;

    @Schema(description = ContentEditorConstant.CONTENT_SCHEMA_DESCRIPTION)
    private ContentDocument content;

    @Schema(description = ContentEditorConstant.EDITOR_SCHEMA_VERSION_DESCRIPTION)
    private Integer editorSchemaVersion;

    @Schema(description = "分类标签")
    private String categoryLabel;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "关联商品ID列表（派生只读字段，由正文中的 product_group.spuIds 聚合返回）")
    private List<Long> spuIds;
}
