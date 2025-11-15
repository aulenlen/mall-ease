package com.mallease.pms.dto.query;

import com.mallease.common.dto.query.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品属性查询对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品属性查询对象")
public class ProductAttributeQuery extends BaseQuery {

    @Schema(description = "产品属性分类ID")
    private Long productAttributeCategoryId;

    @Schema(description = "属性名称(模糊查询)")
    private String name;

    @Schema(description = "属性的类型：0->规格；1->参数")
    private Integer type;
}
