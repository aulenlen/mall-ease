package com.mallease.bff.controller.portal.editorial.vo;

import com.mallease.bff.controller.portal.common.vo.RecommendProductRespVO;
import com.mallease.common.dto.content.EditorialContentDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** 编辑精选详情。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "编辑精选详情")
public class EditorialDetailRespVO {

    @Schema(description = "编辑精选ID")
    private Long id;

    @Schema(description = "系列名称")
    private String seriesName;

    @Schema(description = "期号")
    private Integer volumeNo;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String subTitle;

    @Schema(description = "封面图URL")
    private String coverPic;

    @Schema(description = "正文内容")
    private EditorialContentDocument content;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "文中商品")
    private List<RecommendProductRespVO> products;
}
