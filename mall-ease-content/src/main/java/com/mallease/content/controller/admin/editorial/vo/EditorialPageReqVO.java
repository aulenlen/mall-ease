package com.mallease.content.controller.admin.editorial.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "编辑精选分页参数")
public class EditorialPageReqVO extends BaseQuery {

    @Schema(description = "标题关键字")
    private String keyword;

    @Schema(description = "系列名称")
    private String seriesName;

    @Schema(description = "状态：0-草稿 1-已发布 2-已下架")
    private Integer status;
}
