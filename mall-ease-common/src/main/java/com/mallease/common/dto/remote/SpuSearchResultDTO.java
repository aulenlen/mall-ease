package com.mallease.common.dto.remote;

import com.mallease.common.api.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品搜索结果（服务间调用）
 *
 * @author: Aulen
 * @create: 2026-01-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpuSearchResultDTO {

    /**
     * 商品列表（分页）
     */
    private Page<SpuRecommendDTO> products;

    /**
     * 聚合筛选项
     */
    private SearchFilterDTO filters;
}