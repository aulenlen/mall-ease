package com.mallease.marketing.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "秒杀活动视图对象")
public class FlashActivityVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "活动开始日期")
    private LocalDate startDate;

    @Schema(description = "活动结束日期")
    private LocalDate endDate;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;
}
