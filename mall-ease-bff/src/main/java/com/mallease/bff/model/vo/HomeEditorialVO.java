package com.mallease.bff.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 首页编辑精选 VO
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "首页编辑精选")
public class HomeEditorialVO {

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

    @Schema(description = "作者")
    private String author;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "关联商品ID列表")
    private List<Long> spuIds;
}