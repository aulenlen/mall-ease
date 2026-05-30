package com.mallease.content.controller.admin.media.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "素材分页查询参数")
public class MediaPageReqVO extends BaseQuery {

    @Schema(description = "分组ID：不传查全部")
    @Min(value = 0, message = "分组ID不能小于0")
    private Long groupId;

    @Schema(description = "媒体类型：IMAGE/VIDEO/OTHER")
    private String mediaType;

    @Schema(description = "关键字（文件名）")
    private String keyword;
}
