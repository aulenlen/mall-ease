package com.mallease.app.model.vo;

import com.mallease.common.api.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品搜索结果
 *
 * @author: Aulen
 * @create: 2026-01-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品搜索结果")
public class ProductSearchResultVO {

    @Schema(description = "商品列表（分页）")
    private Page<ProductItemVO> products;

    @Schema(description = "聚合筛选项")
    private SearchFilterVO filters;
}