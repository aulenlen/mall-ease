package com.mallease.content.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 编辑精选响应对象
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Schema(description = "编辑精选响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditorialVO {

    @Schema(description = "主键ID")
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

    @Schema(description = "状态：0-草稿 1-已发布 2-已下架")
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