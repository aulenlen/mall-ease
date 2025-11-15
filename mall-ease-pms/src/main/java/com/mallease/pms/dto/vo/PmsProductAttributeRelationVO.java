package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品属性关联视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "商品属性关联")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductAttributeRelationVO {

    @Schema(description = "属性ID")
    private Long attributeId;

    @Schema(description = "属性分类ID")
    private Long attributeCategoryId;
}
