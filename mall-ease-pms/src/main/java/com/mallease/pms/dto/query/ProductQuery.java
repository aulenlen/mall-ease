package com.mallease.pms.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品列表查询条件
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "商品查询条件")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductQuery {

    @Schema(description = "上架状态(0:下架 1:上架)")
    private Integer publishStatus;

    @Schema(description = "审核状态(0:未审核 1:审核通过)")
    private Integer verifyStatus;

    @Schema(description = "商品名称关键字")
    private String keyword;

    @Schema(description = "商品货号")
    private String productSn;

    @Schema(description = "商品分类ID")
    private Long productCategoryId;

    @Schema(description = "商品品牌ID")
    private Long brandId;

    @Schema(description = "每页数量")
    @Builder.Default
    @Min(value = 1, message = "每页数量不能小于1")
    private Integer pageSize = 5;

    @Schema(description = "页码")
    @Builder.Default
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
}
