package com.mallease.content.controller.admin.preferencearea.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "优选专区分页参数")
public class PreferenceAreaPageReqVO extends BaseQuery {

    @Schema(description = "名称关键字")
    private String name;

    @Schema(description = "显示状态")
    private Integer showStatus;
}
