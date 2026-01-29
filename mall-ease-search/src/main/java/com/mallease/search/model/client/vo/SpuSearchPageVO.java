package com.mallease.search.model.client.vo;

import com.mallease.common.api.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品搜索页面响应 VO
 *
 * @author: Aulen
 * @create: 2026-01-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商品搜索页面响应")
public class SpuSearchPageVO {

    @Schema(description = "商品列表（分页）")
    private Page<SpuItemVO> products;

    @Schema(description = "筛选面板（聚合结果）")
    private SearchFilterVO filters;
}
