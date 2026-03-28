package com.mallease.content.controller.admin.preferencearea.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "优选专区列表响应")
public class PreferenceAreaListRespVO {

    @Schema(description = "优选专区ID")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "显示状态")
    private Integer showStatus;

    @Schema(description = "显示状态名称")
    private String showStatusName;
}
