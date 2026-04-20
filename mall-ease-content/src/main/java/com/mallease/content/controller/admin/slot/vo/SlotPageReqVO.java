package com.mallease.content.controller.admin.slot.vo;

import com.mallease.common.dto.client.BaseQuery;
import com.mallease.content.constant.ContentStatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "槽位分页参数")
public class SlotPageReqVO extends BaseQuery {

    @Schema(description = "关键字（编码或名称）")
    private String keyword;

    @Schema(description = "页面编码")
    private String pageCode;

    @Schema(description = "渲染类型")
    private String renderType;

    @Schema(description = ContentStatusConstants.ENABLE_STATUS_SCHEMA)
    private Integer status;
}
