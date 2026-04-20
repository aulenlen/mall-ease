package com.mallease.bff.controller.portal.home.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页轮播图 VO
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "首页轮播图")
public class HomeBannerRespVO {

    @Schema(description = "Banner ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "副标题")
    private String subTitle;

    @Schema(description = "图片URL")
    private String pic;

    @Schema(description = "跳转类型：0-无跳转 1-活动页 2-商品详情 3-内容文章 4-外链")
    private Integer type;

    @Schema(description = "跳转目标ID")
    private Long targetId;

    @Schema(description = "跳转链接（外链时使用）")
    private String url;
}
