package com.mallease.content.model.client.query;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 编辑精选查询对象
 *
 * @author: Aulen
 * @create: 2026-01-07
 */
@Schema(description = "编辑精选查询")
@Data
@EqualsAndHashCode(callSuper = true)
public class EditorialQuery extends BaseQuery {

    @Schema(description = "关键字（标题模糊匹配）")
    private String keyword;

    @Schema(description = "系列名称")
    private String seriesName;

    @Schema(description = "状态：0-草稿 1-已发布 2-已下架")
    private Integer status;
}