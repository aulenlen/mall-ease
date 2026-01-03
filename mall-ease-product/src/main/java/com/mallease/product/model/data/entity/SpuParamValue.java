package com.mallease.product.model.data.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * SPU参数值表（存储 SPU 级别的参数属性值，非规格属性）
 *
 * @author: Aulen
 * @create: 2025-12-10
 */
@Data
public class SpuParamValue {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 属性ID
     */
    private Long paramId;

    /**
     * 属性值
     */
    private String value;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
