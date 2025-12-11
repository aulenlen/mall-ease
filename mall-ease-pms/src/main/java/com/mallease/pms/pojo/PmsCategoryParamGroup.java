package com.mallease.pms.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类与参数组关联表
 * 说明：建立分类与参数组的多对多关系
 * 一个分类可以关联多个参数组，一个参数组也可以被多个分类使用
 * 示例：手机分类关联"基本参数组"（含CPU型号、屏幕尺寸等参数）
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