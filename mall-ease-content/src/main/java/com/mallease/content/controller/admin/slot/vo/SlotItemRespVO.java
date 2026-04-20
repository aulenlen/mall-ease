package com.mallease.content.controller.admin.slot.vo;

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
@Schema(description = "槽位投放项响应")
public class SlotItemRespVO {

    @Schema(description = "投放项ID")
    private Long id;

    @Schema(description = "槽位ID")
    private Long slotId;

    @Schema(description = "投放项类型")
    private String itemType;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String subTitle;

    @Schema(description = "图片URL")
    private String pic;

    @Schema(description = "跳转类型")
    private Integer jumpType;

    @Schema(description = "跳转目标ID")
    private Long jumpTargetId;

    @Schema(description = "跳转链接")
    private String url;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
