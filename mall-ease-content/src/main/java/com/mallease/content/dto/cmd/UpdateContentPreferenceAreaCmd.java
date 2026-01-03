package com.mallease.content.dto.cmd;

import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 更新优选专区命令
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
public class UpdateContentPreferenceAreaCmd {

    @NotNull(message = "专区ID不能为空")
    private Long id;

    @Size(max = 255, message = "名称长度不能超过255个字符")
    private String name;

    @Size(max = 255, message = "副标题长度不能超过255个字符")
    private String subTitle;

    private byte[] pic;

    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Min(value = 0, message = "显示状态值必须为0或1")
    @Max(value = 1, message = "显示状态值必须为0或1")
    private Integer showStatus;
}
