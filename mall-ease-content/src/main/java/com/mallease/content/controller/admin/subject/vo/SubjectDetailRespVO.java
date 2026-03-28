package com.mallease.content.controller.admin.subject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "专题详情响应")
public class SubjectDetailRespVO {

    @Schema(description = "专题ID")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "专题名称")
    private String title;

    @Schema(description = "专题图片")
    private String pic;

    @Schema(description = "关联商品数量")
    private Integer spuCount;

    @Schema(description = "推荐状态")
    private Integer recommendStatus;

    @Schema(description = "推荐状态名称")
    private String recommendStatusName;

    @Schema(description = "专题内容")
    private String content;

    @Schema(description = "收藏数量")
    private Integer collectCount;

    @Schema(description = "阅读数量")
    private Integer readCount;

    @Schema(description = "评论数量")
    private Integer commentCount;

    @Schema(description = "相册图片")
    private String albumPics;

    @Schema(description = "专题描述")
    private String description;

    @Schema(description = "显示状态")
    private Integer showStatus;

    @Schema(description = "显示状态名称")
    private String showStatusName;

    @Schema(description = "转发数")
    private Integer forwardCount;

    @Schema(description = "创建时间")
    private String createTimeStr;
}
