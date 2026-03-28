package com.mallease.content.controller.admin.preferencearea.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "优选专区保存参数")
public class PreferenceAreaReqVO {

    public interface Create {}

    public interface Update {}

    @Schema(description = "优选专区ID")
    @NotNull(groups = Update.class, message = "更新时ID不能为空")
    private Long id;

    @Schema(description = "名称")
    @NotBlank(groups = Create.class, message = "名称不能为空")
    @Size(max = 255, message = "名称长度不能超过255个字符")
    private String name;

    @Schema(description = "副标题")
    @Size(max = 255, message = "副标题长度不能超过255个字符")
    private String subTitle;

    @Schema(description = "展示图片")
    private byte[] pic;

    @Schema(description = "排序值")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    @Schema(description = "显示状态：0-不显示 1-显示")
    @Min(value = 0, message = "显示状态值必须为0或1")
    @Max(value = 1, message = "显示状态值必须为0或1")
    private Integer showStatus;
}
