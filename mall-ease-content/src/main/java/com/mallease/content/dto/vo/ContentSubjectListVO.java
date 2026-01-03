package com.mallease.content.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专题列表响应类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "专题列表响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSubjectListVO {

    @Schema(description = "专题ID")
    private Long id;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "专题名称")
    private String title;

    @Schema(description = "专题图片URL")
    private String pic;

    @Schema(description = "关联产品数量")
    private Integer spuCount;

    @Schema(description = "推荐状态(0:不推荐 1:推荐)")
    private Integer recommendStatus;

    @Schema(description = "推荐状态名称")
    private String recommendStatusName;

    @Schema(description = "收藏数量")
    private Integer collectCount;

    @Schema(description = "阅读数量")
    private Integer readCount;

    @Schema(description = "显示状态(0:不显示 1:显示)")
    private Integer showStatus;

    @Schema(description = "显示状态名称")
    private String showStatusName;

    @Schema(description = "创建时间")
    private String createTimeStr;
}
