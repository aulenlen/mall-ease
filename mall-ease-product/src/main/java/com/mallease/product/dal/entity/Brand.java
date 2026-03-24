package com.mallease.product.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 品牌表
 *
 * @author: Aulen
 * @create: 2025-11-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {
    /**
     * 主键ID
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
     * 排序
     */
    private Integer sort;

    /**
     * 是否为品牌制造商：0->不是；1->是
     */
    private Integer factoryStatus;

    /**
     * 显示状态
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
     * 品牌logo
     */
    private String logo;

    /**
     * 专区大图
     */
    private String bigPic;

    /**
     * 品牌故事
     */
    private String brandStory;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;
}

