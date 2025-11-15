package com.mallease.pms.dto.query;

import com.mallease.common.dto.query.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分类查询对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品分类查询对象")
public class ProductCategoryQuery extends BaseQuery {

    @Schema(description = "上级分类ID")
    private Long parentId;

    @Schema(description = "分类名称(模糊查询)")
    private String name;

    @Schema(description = "分类级别：0->1级；1->2级")
    private Integer level;

    @Schema(description = "是否显示在导航栏：0->不显示；1->显示")
    private Integer navStatus;

    @Schema(description = "显示状态：0->不显示；1->显示")
    private Integer showStatus;
}
