package com.mallease.bff.controller.portal.home.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** 首页文章卡片。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "首页文章")
public class HomeArticleRespVO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String subTitle;

    @Schema(description = "封面图URL")
    private String coverPic;

    @Schema(description = "分类标签")
    private String categoryLabel;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "关联商品ID列表")
    private List<Long> spuIds;
}
