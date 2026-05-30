package com.mallease.content.controller.admin.media.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "素材分组请求")
public class MediaGroupReqVO {

    public interface Create {
    }

    public interface Update {
    }

    @Schema(description = "分组名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(groups = {Create.class, Update.class}, message = "分组名称不能为空")
    @Size(groups = {Create.class, Update.class}, max = 64, message = "分组名称长度不能超过64个字符")
    private String name;

    @Schema(description = "排序值，越小越靠前", example = "0")
    @Min(groups = {Create.class, Update.class}, value = 0, message = "排序值不能小于0")
    private Integer sort;
}
