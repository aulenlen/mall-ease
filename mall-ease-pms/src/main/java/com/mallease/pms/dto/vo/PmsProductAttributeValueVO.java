package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品属性值视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "产品属性值")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductAttributeValueVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "产品属性ID")
    private Long productAttributeId;

    @Schema(description = "手动添加规格或参数的值，参数单值，规格有多个时以逗号隔开")
    private String value;
}
