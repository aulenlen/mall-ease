package com.mallease.pms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品属性值请求参数
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductAttributeValueRequest {
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
    @NotNull(message = "产品属性ID不能为空")
    private Long productAttributeId;

    /**
     * 手动添加规格或参数的值，参数单值，规格有多个时以逗号隔开
     */
    private String value;
}
