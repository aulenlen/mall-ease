package com.mallease.content.controller.admin.slot.vo;

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
@Schema(description = "槽位响应")
public class SlotRespVO {

    @Schema(description = "槽位ID")
    private Long id;

    @Schema(description = "槽位编码")
    private String code;

    @Schema(description = "槽位名称")
    private String name;

    @Schema(description = "页面编码")
    private String pageCode;

    @Schema(description = "渲染类型")
    private String renderType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
