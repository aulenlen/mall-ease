package com.mallease.product.model.client.query;

import com.mallease.common.dto.client.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * SPU查询对象
 * <p>
 * 支持多维度组合查询：品牌、分类、状态、价格区间、关键字等
 *
 * @author: Aulen
 * @create: 2025-12-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "SPU查询对象")
public class SpuQuery extends BaseQuery {

    @Schema(description = "SPU名称或关键字（模糊查询）")
    private String keyword;

    @Schema(description = "SPU编码（精确查询）")
    private String spuCode;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "商品分类ID")
    private Long categoryId;

    @Schema(description = "上架状态: 0-下架, 1-上架")
    private Integer publishStatus;

    @Schema(description = "新品状态: 0-不是新品, 1-新品")
    private Integer newStatus;

    @Schema(description = "推荐状态: 0-不推荐, 1-推荐")
    private Integer recommendStatus;

    @Schema(description = "审核状态: 0-未审核, 1-审核通过")
    private Integer verifyStatus;

    @Schema(description = "最低价格下限（区间查询）")
    private BigDecimal minPriceStart;

    @Schema(description = "最低价格上限（区间查询）")
    private BigDecimal minPriceEnd;

    @Schema(description = "创建时间开始（格式：yyyy-MM-dd HH:mm:ss）")
    private String createTimeStart;

    @Schema(description = "创建时间结束（格式：yyyy-MM-dd HH:mm:ss）")
    private String createTimeEnd;
}
