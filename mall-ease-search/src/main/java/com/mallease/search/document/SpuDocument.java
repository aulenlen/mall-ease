package com.mallease.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品搜索文档
 * 对应 Elasticsearch 索引：mall_spu_v1
 *
 * @author: Aulen
 * @create: 2025-11-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "mall_spu_v1")
@Setting(shards = 1, replicas = 0)
public class SpuDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long spuId;

    @Field(type = FieldType.Keyword)
    private String spuCode;

    /**
     * SPU 名称 - 主搜索字段
     * 支持：中文分词 + 拼音搜索 + 精确匹配
     */
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "ik_smart_pinyin")
            }
    )
    private String name;

    /**
     * 副标题 - 次级搜索字段
     */
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "ik_smart_pinyin")
            }
    )
    private String subTitle;

    /**
     * 关键词 - 搜索增强
     */
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "ik_smart_pinyin")
            }
    )
    private String keywords;

    /**
     * 品牌ID - 筛选用
     */
    @Field(type = FieldType.Long)
    private Long brandId;

    /**
     * 品牌名称 - 搜索 + 展示
     */
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "ik_smart_pinyin")
            }
    )
    private String brandName;

    /**
     * 分类ID（叶子节点）- 筛选用
     */
    @Field(type = FieldType.Long)
    private Long categoryId;

    /**
     * 分类路径 - 支持多级分类筛选
     * 格式："/1/7/8/"，支持前缀匹配
     */
    @Field(type = FieldType.Keyword)
    private String categoryPath;

    /**
     * 分类名称 - 展示用
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 最低价（所有 SKU 中的最低价）
     */
    @Field(type = FieldType.Double)
    private BigDecimal minPrice;

    /**
     * 最高价（所有 SKU 中的最高价）
     */
    @Field(type = FieldType.Double)
    private BigDecimal maxPrice;

    /**
     * 是否有货（任一 SKU 有库存即为 true）
     */
    @Field(type = FieldType.Boolean)
    private Boolean inStock;

    /**
     * 上架状态：0-下架 1-上架
     */
    @Field(type = FieldType.Integer)
    private Integer publishStatus;

    /**
     * 新品状态：0-否 1-是
     */
    @Field(type = FieldType.Integer)
    private Integer newStatus;

    /**
     * 推荐状态：0-否 1-是
     */
    @Field(type = FieldType.Integer)
    private Integer recommendStatus;

    /**
     * 排序值
     */
    @Field(type = FieldType.Integer)
    private Integer sort;

    /**
     * 销量（用于销量排序）
     */
    @Field(type = FieldType.Integer)
    private Integer sale;

    /**
     * 主图URL - 列表展示用
     */
    @Field(type = FieldType.Keyword, index = false)
    private String pic;

    /**
     * SKU 列表 - 价格区间展示
     */
    @Field(type = FieldType.Nested)
    private List<SpuSkuDocument> skuList;

    /**
     * 规格列表 - 聚合筛选用
     */
    @Field(type = FieldType.Nested)
    private List<SpuSpecValueDocument> specValueList;

    /**
     * 参数属性列表 - 搜索展示用
     */
    @Field(type = FieldType.Nested)
    private List<SpuParamValueDocument> paramValueList;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date createTime;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date updateTime;
}