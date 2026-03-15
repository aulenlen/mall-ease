package com.mallease.marketing.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "前台秒杀场次视图对象")
public class FlashPortalSessionVO {

    @Schema(description = "场次ID")
    private Long id;

    @Schema(description = "场次名称")
    private String name;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "时间状态：0-未开始 1-进行中 2-已结束")
    private Integer timeStatus;
}
