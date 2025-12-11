package com.mallease.pms.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类与规格组关联表
 * 说明：建立分类与规格组的多对多关系
 * 一个分类可以关联多个规格组，一个规格组也可以被多个分类使用
 * 示例：手机分类关联"手机规格组"（含颜色、内存等规格）
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
