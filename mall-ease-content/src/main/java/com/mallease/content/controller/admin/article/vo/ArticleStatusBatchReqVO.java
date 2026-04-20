package com.mallease.content.controller.admin.article.vo;

import com.mallease.content.constant.ContentStatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章批量更新状态请求 VO
 *
 * @author: Aulen
 * @create: 2026-04-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章批量更新状态参数")
public class ArticleStatusBatchReqVO {

    @Schema(description = "文章ID列表")
    @NotEmpty(message = "文章ID列表不能为空")
    private List<@NotNull(message = "文章ID不能为空") Long> ids;

    @Schema(description = ContentStatusConstants.ARTICLE_STATUS_SCHEMA, example = "1")
    @NotNull(message = "状态不能为空")
    @Min(value = ContentStatusConstants.ARTICLE_STATUS_DRAFT, message = ContentStatusConstants.ARTICLE_STATUS_INVALID_MESSAGE)
    @Max(value = ContentStatusConstants.ARTICLE_STATUS_OFFLINE, message = ContentStatusConstants.ARTICLE_STATUS_INVALID_MESSAGE)
    private Integer status;
}
