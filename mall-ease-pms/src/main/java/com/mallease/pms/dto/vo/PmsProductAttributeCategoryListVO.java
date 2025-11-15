package com.mallease.pms.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品属性分类列表响应类
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductAttributeCategoryListVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 属性数量
     */
    private Integer attributeCount;

    /**
     * 参数数量
     */
    private Integer paramCount;
}
