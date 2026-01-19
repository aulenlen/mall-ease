package com.mallease.product.model.data.entity;

import lombok.Data;

/**
 * 属性值聚合结果（用于聚合查询）
 *
 * @author: Aulen
 * @create: 2026-01-18
 */
@Data
public class AttrValueAggregation {
    private Long attrId;
    private String attrName;
    private String value;
    private Long count;
}