package com.mallease.content.controller.admin.slot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mallease.content.constant.ContentStatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "槽位投放项保存参数")
public class SlotItemReqVO {

    @Schema(description = "投放项ID，更新时传")
    private Long id;

    @Schema(description = "文章ID（ARTICLE 类型必填）")
    private Long articleId;

    @Schema(description = "标题（CARD 类型可用）")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @Schema(description = "摘要（CARD 类型可用）")
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String subTitle;

    @Schema(description = "图片URL（CARD 类型必填）")
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String pic;

    @Schema(description = "跳转类型：见 JumpType 枚举（0-无跳转 1-活动页 2-商品详情 3-内容文章 4-外链）", example = "3")
    @Min(value = 0, message = "跳转类型最小值为0")
    @Max(value = 4, message = "跳转类型最大值为4")
    private Integer jumpType;

    @Schema(description = "跳转目标ID")
    private Long jumpTargetId;

    @Schema(description = "跳转链接")
    @Size(max = 500, message = "跳转链接长度不能超过500个字符")
    private String url;

    @Schema(description = "排序值，越小越靠前", example = "0")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = ContentStatusConstants.ENABLE_STATUS_SCHEMA, example = "1")
    @Min(value = ContentStatusConstants.ENABLE_STATUS_DISABLED, message = ContentStatusConstants.ENABLE_STATUS_INVALID_MESSAGE)
    @Max(value = ContentStatusConstants.ENABLE_STATUS_ENABLED, message = ContentStatusConstants.ENABLE_STATUS_INVALID_MESSAGE)
    private Integer status;

    @Schema(description = "生效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String note;
}
