package com.mallease.content.controller.admin.subject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "专题保存参数")
public class SubjectReqVO {

    public interface Create {}

    public interface Update {}

    @Schema(description = "专题ID")
    @NotNull(groups = Update.class, message = "更新时ID不能为空")
    private Long id;

    @Schema(description = "分类ID")
    @NotNull(groups = Create.class, message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "专题标题")
    @NotBlank(groups = Create.class, message = "专题名称不能为空")
    @Size(max = 100, message = "专题名称长度不能超过100个字符")
    private String title;

    @Schema(description = "专题图片URL")
    @Size(max = 500, message = "专题图片URL长度不能超过500个字符")
    private String pic;

    @Schema(description = "关联商品数量")
    private Integer spuCount;

    @Schema(description = "推荐状态：0-不推荐 1-推荐")
    @Min(value = 0, message = "推荐状态值必须为0或1")
    @Max(value = 1, message = "推荐状态值必须为0或1")
    private Integer recommendStatus;

    @Schema(description = "专题内容")
    private String content;

    @Schema(description = "收藏数量")
    private Integer collectCount;

    @Schema(description = "阅读数量")
    private Integer readCount;

    @Schema(description = "评论数量")
    private Integer commentCount;

    @Schema(description = "相册图片")
    @Size(max = 1000, message = "相册图片URL长度不能超过1000个字符")
    private String albumPics;

    @Schema(description = "专题描述")
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;

    @Schema(description = "显示状态：0-不显示 1-显示")
    @Min(value = 0, message = "显示状态值必须为0或1")
    @Max(value = 1, message = "显示状态值必须为0或1")
    private Integer showStatus;

    @Schema(description = "转发数")
    private Integer forwardCount;
}
