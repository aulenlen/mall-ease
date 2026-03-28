package com.mallease.content.controller.admin.subject.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "专题分页参数")
public class SubjectPageReqVO extends BaseQuery {

    @Schema(description = "专题关键字")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "推荐状态")
    private Integer recommendStatus;

    @Schema(description = "显示状态")
    private Integer showStatus;
}
