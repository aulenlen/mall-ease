package com.mallease.content.controller.admin.subject.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "专题响应")
public class SubjectRespVO {

    @Schema(description = "专题ID")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "专题名称")
    private String title;

    @Schema(description = "专题图片")
    private String pic;

    @Schema(description = "关联商品数量")
    private Integer spuCount;

    @Schema(description = "推荐状态")
    private Integer recommendStatus;

    @Schema(description = "显示状态")
    private Integer showStatus;
}
