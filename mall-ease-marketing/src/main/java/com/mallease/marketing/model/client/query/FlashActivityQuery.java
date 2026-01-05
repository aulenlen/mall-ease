package com.mallease.marketing.model.client.query;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 秒杀活动查询对象
 *
 * @author: Aulen
 * @create: 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "秒杀活动查询对象")
public class FlashActivityQuery extends BaseQuery {

    @Schema(description = "活动标题（模糊查询）")
    private String keyword;

    @Schema(description = "活动开始日期")
    private LocalDate startDate;

    @Schema(description = "活动结束日期")
    private LocalDate endDate;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;
}