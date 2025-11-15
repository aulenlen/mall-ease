package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品属性分类项视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "商品属性分类项")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmsProductAttributeCategoryItemVO {

    @Schema(description = "属性分类ID")
    private Long id;

    @Schema(description = "属性分类名称")
    private String name;

    @Schema(description = "属性数量")
    private Integer attributeCount;

    @Schema(description = "参数数量")
    private Integer paramCount;

    @Schema(description = "商品属性列表")
    private List<ProductAttributeItemVO> productAttributeList;

    /**
     * 商品属性项
     */
    @Schema(description = "商品属性项")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductAttributeItemVO {

        @Schema(description = "属性ID")
        private Long id;

        @Schema(description = "属性分类ID")
        private Long productAttributeCategoryId;

        @Schema(description = "属性名称")
        private String name;

        @Schema(description = "选择类型(0:唯一 1:单选 2:多选)")
        private Integer selectType;

        @Schema(description = "录入方式(0:手工录入 1:从列表中选取)")
        private Integer inputType;

        @Schema(description = "可选值列表(逗号分隔)")
        private String inputList;

        @Schema(description = "排序")
        private Integer sort;

        @Schema(description = "筛选类型(0:普通 1:颜色)")
        private Integer filterType;

        @Schema(description = "检索类型(0:不需要检索 1:关键字检索 2:范围检索)")
        private Integer searchType;

        @Schema(description = "相同属性产品是否关联(0:不关联 1:关联)")
        private Integer relatedStatus;

        @Schema(description = "是否支持手动新增(0:不支持 1:支持)")
        private Integer handAddStatus;

        @Schema(description = "属性类型(0:规格 1:参数)")
        private Integer type;
    }
}
