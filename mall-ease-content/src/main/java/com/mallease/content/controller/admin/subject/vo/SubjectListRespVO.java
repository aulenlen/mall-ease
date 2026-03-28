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
@Schema(description = "专题列表响应")
public class SubjectListRespVO {

    @Schema(description = "专题ID")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "专题名称")
    private String title;

    @Schema(description = "专题图片URL")
    private String pic;

    @Schema(description = "关联商品数量")
    private Integer spuCount;

    @Schema(description = "推荐状态")
    private Integer recommendStatus;

    @Schema(description = "推荐状态名称")
    private String recommendStatusName;

    @Schema(description = "收藏数量")
    private Integer collectCount;

    @Schema(description = "阅读数量")
    private Integer readCount;

    @Schema(description = "显示状态")
    private Integer showStatus;

    @Schema(description = "显示状态名称")
    private String showStatusName;

    @Schema(description = "创建时间")
    private String createTimeStr;
}
