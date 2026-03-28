package com.mallease.content.controller.admin.banner.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "轮播图分页参数")
public class BannerPageReqVO extends BaseQuery {

    @Schema(description = "名称关键字")
    private String keyword;
}
