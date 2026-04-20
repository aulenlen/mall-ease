package com.mallease.content.controller.admin.slot.vo;

import com.mallease.common.dto.client.BaseQuery;
import com.mallease.content.constant.ContentStatusConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "槽位投放项分页参数")
public class SlotItemPageReqVO extends BaseQuery {

    @Schema(description = "槽位ID")
    private Long slotId;

    @Schema(description = "投放项类型")
    private String itemType;

    @Schema(description = ContentStatusConstants.ENABLE_STATUS_SCHEMA)
    private Integer status;
}
