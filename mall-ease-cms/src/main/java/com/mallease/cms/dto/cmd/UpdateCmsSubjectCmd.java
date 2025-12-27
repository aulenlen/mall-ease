package com.mallease.cms.dto.cmd;

import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 更新专题命令
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
public class UpdateCmsSubjectCmd {

    @NotNull(message = "专题ID不能为空")
    private Long id;

    private Long categoryId;

    @Size(max = 100, message = "专题名称长度不能超过100个字符")
    private String title;

    @Size(max = 500, message = "专题图片URL长度不能超过500个字符")
    private String pic;

    private Integer spuCount;

    @Min(value = 0, message = "推荐状态值必须为0或1")
    @Max(value = 1, message = "推荐状态值必须为0或1")
    private Integer recommendStatus;

    private String content;

    private Integer collectCount;

    private Integer readCount;

    private Integer commentCount;

    @Size(max = 1000, message = "相册图片URL长度不能超过1000个字符")
    private String albumPics;

    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;

    @Min(value = 0, message = "显示状态值必须为0或1")
    @Max(value = 1, message = "显示状态值必须为0或1")
    private Integer showStatus;

    private Integer forwardCount;
}
