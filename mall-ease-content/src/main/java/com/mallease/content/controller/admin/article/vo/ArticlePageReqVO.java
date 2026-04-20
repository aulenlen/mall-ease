package com.mallease.content.controller.admin.article.vo;

import com.mallease.common.dto.client.BaseQuery;
import com.mallease.content.constant.ContentStatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文章分页参数")
public class ArticlePageReqVO extends BaseQuery {

    @Schema(description = "标题关键字")
    private String keyword;

    @Schema(description = ContentStatusConstants.ARTICLE_STATUS_SCHEMA)
    private Integer status;
}
