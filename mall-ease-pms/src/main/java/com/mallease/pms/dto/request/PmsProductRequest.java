package com.mallease.pms.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品列表查询请求参数
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductRequest {
    /**
     * 上架状态：0->下架；1->上架
     */
    private Integer publishStatus;

    /**
     * 审核状态：0->未审核；1->审核通过
     */
    private Integer verifyStatus;

    /**
     * 商品名称模糊关键字
     */
    private String keyword;

    /**
     * 商品货号
     */
    private String productSn;

    /**
     * 商品分类编号
     */
    private Long productCategoryId;

    /**
     * 商品品牌编号
     */
    private Long brandId;

    /**
     * 每页数量
     */
    @Builder.Default
    @Min(value = 1, message = "每页数量不能小于1")
    private Integer pageSize = 5;

    /**
     * 页码
     */
    @Builder.Default
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
}
