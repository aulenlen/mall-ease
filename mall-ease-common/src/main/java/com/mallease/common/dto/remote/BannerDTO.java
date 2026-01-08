package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Banner 传输对象（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BannerDTO {

    private Long id;

    private String name;

    private String pic;

    /**
     * 跳转类型：0-无跳转 1-活动页 2-商品详情 3-专题页 4-外链 5-优选专区
     */
    private Integer type;

    private Long targetId;

    private String url;

    private Integer sort;
}