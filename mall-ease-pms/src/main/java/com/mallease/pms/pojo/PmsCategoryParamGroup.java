package com.mallease.pms.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类与参数组关联表
 *
 * @author: Aulen
 * @create: 2025-12-11
 */
@Data
public class PmsCategoryParamGroup {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 参数组ID
     */
    private Long paramGroupId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}