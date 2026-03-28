package com.mallease.content.controller.admin.banner.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "轮播图响应")
public class BannerRespVO {

    @Schema(description = "轮播图ID")
    private Long id;

    @Schema(description = "轮播图名称")
    private String name;

    @Schema(description = "图片URL")
    private String pic;

    @Schema(description = "跳转类型")
    private Integer type;

    @Schema(description = "跳转目标ID")
    private Long targetId;

    @Schema(description = "跳转链接")
    private String url;

    @Schema(description = "投放位置")
    private String position;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    @Schema(description = "点击次数")
    private Integer clickCount;

    @Schema(description = "备注")
    private String note;
}
