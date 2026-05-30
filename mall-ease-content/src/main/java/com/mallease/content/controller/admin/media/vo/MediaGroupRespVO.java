package com.mallease.content.controller.admin.media.vo;

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
@Schema(description = "素材分组响应")
public class MediaGroupRespVO {

    @Schema(description = "分组ID")
    private Long id;

    @Schema(description = "分组名称")
    private String name;

    @Schema(description = "排序值，越小越靠前")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
