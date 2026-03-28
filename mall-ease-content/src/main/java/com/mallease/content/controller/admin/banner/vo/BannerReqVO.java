package com.mallease.content.controller.admin.banner.vo;

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
@Schema(description = "轮播图保存参数")
public class BannerReqVO {

    public interface Create {}

    public interface Update {}

    @Schema(description = "轮播图ID")
    @NotNull(groups = Update.class, message = "更新时ID不能为空")
    private Long id;

    @Schema(description = "轮播图名称", example = "新年促销")
    @NotBlank(groups = Create.class, message = "创建时名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100个字符")
    private String name;

    @Schema(description = "图片URL", example = "https://cdn.example.com/banner.jpg")
    @NotBlank(groups = Create.class, message = "创建时图片URL不能为空")
    @Size(max = 500, message = "图片链接不能超过500个字符")
    private String pic;

    @Schema(description = "跳转类型：0-无跳转 1-活动页 2-商品详情 3-专题页 4-外链 5-优选专区", example = "0")
    @Min(value = 0, message = "跳转类型最小值为0")
    @Max(value = 5, message = "跳转类型最大值为5")
    private Integer type;

    @Schema(description = "跳转目标ID")
    private Long targetId;

    @Schema(description = "跳转链接")
    @Size(max = 500, message = "外链长度不能超过500个字符")
    private String url;

    @Schema(description = "投放位置", example = "home")
    @Size(max = 50, message = "投放位置长度不能超过50个字符")
    private String position;

    @Schema(description = "排序值，越小越靠前", example = "0")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = "状态：0-禁用 1-启用", example = "1")
    @Min(value = 0, message = "状态值只能是0或1")
    @Max(value = 1, message = "状态值只能是0或1")
    private Integer status;

    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    @Schema(description = "备注")
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String note;
}
