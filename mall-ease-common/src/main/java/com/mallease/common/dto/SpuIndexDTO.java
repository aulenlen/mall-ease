package com.mallease.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuIndexDTO {
    private static final long serialVersionUID = 1L;

    private Long spuId;

    private String spuCode;

    /**
     * SPU 名称 - 主搜索字段
     * 支持：中文分词 + 拼音搜索 + 精确匹配
     */
    private String name;

    /**
     * 副标题 - 次级搜索字段
     */
    private String subTitle;

    /**
     * 关键词 - 搜索增强
     */
    private String keywords;

    /**
     * 品牌ID - 筛选用
     */
    private Long brandId;

    /**
     * 品牌名称 - 搜索 + 展示
     */
    private String brandName;

    /**
     * 分类ID（叶子节点）- 筛选用
     */
    private Long categoryId;

    /**
     * 分类路径 - 支持多级分类筛选
     * 格式："/1/7/8/"，支持前缀匹配
     */
    private String categoryPath;

    /**
     * 分类名称 - 展示用
     */
    private String categoryName;

    /**
     * 最低价（所有 SKU 中的最低价）
     */
    private BigDecimal minPrice;

    /**
     * 最高价（所有 SKU 中的最高价）
     */
    private BigDecimal maxPrice;

    /**
     * 是否有货（任一 SKU 有库存即为 true）
     */
    private Boolean inStock;

    /**
     * 上架状态：0-下架 1-上架
     */
    private Integer publishStatus;

    /**
     * 新品状态：0-否 1-是
     */
    private Integer newStatus;

    /**
     * 推荐状态：0-否 1-是
     */
    private Integer recommendStatus;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 销量（用于销量排序）
     */
    private Integer sale;

    /**
     * 主图URL - 列表展示用
     */
    private String pic;

    /**
     * SKU 列表 - 价格区间展示
     */
    private List<Sku> skuList;

    /**
     * 规格列表 - 聚合筛选用
     */
    private List<SpecValue>  specValueList;;

    /**
     * 参数属性列表 - 搜索展示用
     */
    private List<ParamValue> paramValueList;

    private Date createTime;

    private Date updateTime;

    /**
     * SKU信息（嵌套对象）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sku implements Serializable {
        /**
         * SKU ID
         */
        private Long skuId;

        /**
         * SKU 编码
         */
        private String skuCode;

        /**
         * SKU 价格
         */
        private BigDecimal price;
    }

    /**
     * 规格值（嵌套对象）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecValue implements Serializable {
        /**
         * 规格ID（pms_spec.id）
         */
        private Long specId;

        /**
         * 规格名称，如 "颜色"、"内存"
         */
        private String specName;

        /**
         * 规格值，如 "黑色"、"128G"
         */
        private String specValue;
    }

    /**
     * 属性值（嵌套对象）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParamValue implements Serializable {
        /**
         * 参数ID（pms_param.id）
         */
        private Long paramId;

        /**
         * 参数名称，如 "CPU型号"、"屏幕尺寸"
         */
        private String paramName;

        /**
         * 参数值，如 "骁龙8 Gen3"、"6.82英寸"
         */
        private String paramValue;
    }
}
