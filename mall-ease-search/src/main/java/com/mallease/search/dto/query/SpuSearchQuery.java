package com.mallease.search.dto.query;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品搜索查询对象
 *
 * @author: Aulen
 * @create: 2025-12-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品搜索查询对象")
public class SpuSearchQuery extends BaseQuery {

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "品牌ID列表")
    private List<Long> brandIds;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类路径（多级分类筛选）")
    private String categoryPath;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "是否有货")
    private Boolean inStock;

    @Schema(description = "新品状态：1-新品")
    private Integer newStatus;

    @Schema(description = "推荐状态：1-推荐")
    private Integer recommendStatus;

    @Schema(description = "规格筛选（格式：specId:specValue）")
    private List<String> specs;

    @Schema(description = "排序类型：0-综合 1-销量 2-价格升序 3-价格降序 4-新品")
    private Integer sortType = 0;

    @Schema(description = "是否需要聚合数据")
    private Boolean needAggregation = false;
}
