package com.mallease.product.controller.admin.spu.vo;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 后台商品分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "后台商品分页查询参数")
public class SpuPageReqVO extends BaseQuery {

    @Schema(description = "商品名称关键字（模糊查询）")
    private String keyword;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer publishStatus;

    @Schema(description = "新品状态: 0-不是新品, 1-新品")
    private Integer newStatus;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    private Integer recommendStatus;

    @Schema(description = "审核状态: 0-未审核, 1-审核通过")
    private Integer verifyStatus;

    @Schema(description = "是否存在未发布草稿: 0-不存在, 1-存在")
    private Integer hasStagedChanges;
}
