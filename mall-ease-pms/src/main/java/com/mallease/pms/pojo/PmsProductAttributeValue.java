package com.mallease.pms.pojo;

import lombok.Data;

/**
 * 存储产品参数信息的表
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
public class PmsProductAttributeValue {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品属性ID
     */
    private Long productAttributeId;

    /**
     * 手动添加规格或参数的值，参数单值，规格有多个时以逗号隔开
     */
    private String value;
}