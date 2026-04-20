package com.mallease.bff.controller.portal.article.vo;

import com.mallease.bff.controller.portal.common.vo.RecommendProductRespVO;
import com.mallease.common.constant.ContentEditorConstant;
import com.mallease.common.dto.content.ContentDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** 文章详情。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章详情")
public class ArticleDetailRespVO {

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

    @Schema(description = "扩展字段")
    private Map<String, Object> extras;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "文中商品池")
    private List<RecommendProductRespVO> products;
}
