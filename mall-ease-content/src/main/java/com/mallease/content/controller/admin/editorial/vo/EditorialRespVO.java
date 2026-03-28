package com.mallease.content.controller.admin.editorial.vo;

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
@Schema(description = "编辑精选响应")
public class EditorialRespVO {

    @Schema(description = "编辑精选ID")
    private Long id;

    @Schema(description = "系列名称")
    private String seriesName;

    @Schema(description = "期号")
    private Integer volumeNo;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String subTitle;

    @Schema(description = "封面图URL")
    private String coverPic;

    @Schema(description = "正文内容")
    private String content;

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

    @Schema(description = "关联商品ID列表")
    private List<Long> spuIds;
}
