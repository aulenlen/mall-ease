package com.mallease.product.controller.admin.spu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台商品统计响应
 */
@Schema(description = "后台商品统计响应")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpuStatsRespVO {

    @Schema(description = "全部商品数量")
    private Long allCount;

    @Schema(description = "已上架商品数量")
    private Long publishedCount;

    @Schema(description = "已下架商品数量")
    private Long unpublishedCount;

    @Schema(description = "未审核商品数量")
    private Long unverifiedCount;
}
