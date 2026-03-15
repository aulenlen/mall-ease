package com.mallease.marketing.model.client.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "秒杀场次查询对象")
public class FlashSessionQuery extends BaseQuery {

    @Schema(description = "场次名称")
    private String name;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    @Schema(description = "开始时间下限")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeFrom;

    @Schema(description = "开始时间上限")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeTo;
}
