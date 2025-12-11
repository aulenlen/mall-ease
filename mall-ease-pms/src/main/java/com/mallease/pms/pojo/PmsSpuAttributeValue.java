package com.mallease.pms.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * SPU参数值表（存储 SPU 级别的参数属性值，非规格属性）
 * 说明：规格属性存储在 SKU.spec_values，参数属性存储在此表
 * 示例：CPU型号、屏幕尺寸、电池容量等
 *
 * @author: Aulen
 * @create: 2025-12-10
 */
@Data
public class PmsSpuAttributeValue {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * 属性ID（pms_product_attribute.id，type=1 的参数属性）
     */
    private Long productAttributeId;

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
