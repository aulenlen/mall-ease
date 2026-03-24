package com.mallease.product.controller.admin.brand.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 品牌列表响应
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandListRespVO {

    /**
     * 品牌ID
     */
    private Long id;

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 首字母
     */
    private String firstLetter;

    /**
     * 品牌logo
     */
    private String logo;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否为品牌制造商：0->不是；1->是
     */
    private Integer factoryStatus;

    /**
     * 显示状态：0->不显示 1->显示
     */
    private Integer showStatus;

    /**
     * SPU数量
     */
    private Integer spuCount;

    /**
     * SPU评论数量
     */
    private Integer spuCommentCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
