package com.mallease.search.model.client.query;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    @Size(max = 100, message = "搜索关键词不能超过100个字符")
    private String keyword;

    @Schema(description = "品牌ID列表")
    @Size(max = 10, message = "品牌筛选最多支持10个")
    private List<Long> brandIds = new ArrayList<>();

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类路径（多级分类筛选）")
    private String categoryPath;

    @Schema(description = "最低价格")
    @Min(value = 0, message = "最低价格不能为负数")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    @Min(value = 0, message = "最高价格不能为负数")
    private BigDecimal maxPrice;

    @Schema(description = "是否有货")
    private Boolean inStock;

    @Schema(description = "新品状态：1-新品")
    @Min(value = 0, message = "新品状态值无效")
    @Max(value = 1, message = "新品状态值无效")
    private Integer newStatus;

    @Schema(description = "推荐状态：1-推荐")
    @Min(value = 0, message = "推荐状态值无效")
    @Max(value = 1, message = "推荐状态值无效")
    private Integer recommendStatus;

    @Schema(description = "规格筛选（格式：specId:specValue）")
    @Size(max = 20, message = "规格筛选最多支持20个")
    private List<String> specs = new ArrayList<>();

    @Schema(description = "排序类型：0-综合 1-销量 2-价格升序 3-价格降序 4-新品")
    @Min(value = 0, message = "排序类型无效")
    @Max(value = 4, message = "排序类型无效")
    private Integer sortType = 0;

    @Schema(description = "是否需要聚合数据")
    private Boolean needAggregation = false;
}