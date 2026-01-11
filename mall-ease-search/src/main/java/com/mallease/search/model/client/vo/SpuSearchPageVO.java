package com.mallease.search.model.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品搜索分页结果（含筛选面板）
 *
 * @author: Aulen
 * @create: 2026-01-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商品搜索分页结果")
public class SpuSearchPageVO {

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "商品列表")
    private List<SpuSearchResultVO> list;

    @Schema(description = "筛选面板（needAggregation=true 时返回）")
    private SearchFilterVO filters;
}