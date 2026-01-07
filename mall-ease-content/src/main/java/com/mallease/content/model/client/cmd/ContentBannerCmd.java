package com.mallease.content.model.client.cmd;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建Banner命令对象
 *
 * @author: Aulen
 * @create: 2025-01-01
 */
@Data
@Schema(description = "创建Banner请求")
public class ContentBannerCmd {

    @Schema(description = "Banner名称", example = "新年促销")
    @NotBlank(message = "名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100个字符")
    private String name;

    @Schema(description = "图片URL", example = "https://cdn.example.com/banner.jpg")
    @NotBlank(message = "图片URL不能为空")
    @Size(max = 500, message = "图片链接不能超过500个字符")
    private String pic;

    @Schema(description = "跳转类型：0-无跳转 1-活动页 2-商品详情 3-专题页 4-外链 5-优选专区", example = "0")
    @Min(value = 0, message = "跳转类型最小值为0")
    @Max(value = 5, message = "跳转类型最大值为5")
    private Integer type;

    @Schema(description = "跳转目标ID（type=1/2/3/5时使用）")
    private Long targetId;

    @Schema(description = "跳转链接（type=4外链时使用）")
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