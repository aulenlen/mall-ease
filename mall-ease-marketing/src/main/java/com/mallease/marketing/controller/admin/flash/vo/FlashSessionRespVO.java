package com.mallease.marketing.controller.admin.flash.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 秒杀场次视图对象
 *
 * @author: Aulen
 * @create: 2026-01-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "秒杀场次视图对象")
public class FlashSessionRespVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "场次名称")
    private String name;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "场次启用状态：0-禁用 1-启用")
    private Integer sessionStatus;

    @Schema(description = "时间状态：0-未开始 1-进行中 2-已结束")
    private Integer timeStatus;
}
