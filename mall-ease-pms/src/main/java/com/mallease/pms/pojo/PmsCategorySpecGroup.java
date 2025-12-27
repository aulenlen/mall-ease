package com.mallease.pms.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类与规格组关联表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class PmsCategorySpecGroup {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 规格组ID
     */
    private Long specGroupId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
