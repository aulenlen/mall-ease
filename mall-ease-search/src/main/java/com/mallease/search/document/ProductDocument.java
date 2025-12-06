package com.mallease.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品搜索文档
 * 对应 Elasticsearch 索引：mall_product_v1
 *
 * @author: Aulen
 * @create: 2025-11-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "mall_product_v1")
@Setting(shards = 1, replicas = 0)
public class ProductDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long productId;

    @Field(type = FieldType.Keyword)
    private Long productSn;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "ik_smart_pinyin")
            }
    )
    private String name;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "ik_smart_pinyin")
            }
    )
    private String subTitle;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "ik_smart_pinyin")
            }
    )
    private String keywords;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Double)
    private Double originalPrice;

    @Field(type = FieldType.Boolean)
    private Boolean inStock;

    @Field(type = FieldType.Long)
    private Long brandId;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "ik_smart_pinyin")
            }
    )
    private String brandName;

    @Field(type = FieldType.Long)
    private Long productCategoryId;

    @Field(type = FieldType.Keyword)
    private String productCategoryName;

    @Field(type = FieldType.Integer)
    private Integer publishStatus;

    @Field(type = FieldType.Integer)
    private Integer newStatus;

    @Field(type = FieldType.Integer)
    private Integer recommendStatus;

    @Field(type = FieldType.Keyword, index = false)
    private String pic;

    @Field(type = FieldType.Keyword, index = false)
    private String albumPics;

    @Field(type = FieldType.Nested)
    private List<EsSkuInfo> skuList;

    @Field(type = FieldType.Nested)
    private List<EsAttrValue>  attrValueList;

    @Field(type = FieldType.Integer)
    private Integer sort;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date createTime;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date updateTime;

    /**
     * SKU信息（嵌套对象）
     * SKU的库存不存储在ES，由Redis管理
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EsSkuInfo implements Serializable {
        @Field(type = FieldType.Keyword)
        private String skuCode;

        @Field(type = FieldType.Double)
        private Double price;

        @Field(type = FieldType.Keyword, index = false)
        private String spData;
    }

    /**
     * 属性值（嵌套对象）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EsAttrValue implements Serializable {
        @Field(type = FieldType.Long)
        private Long productAttributeId;

        @Field(type = FieldType.Keyword)
        private String attributeName;

        @Field(type = FieldType.Keyword)
        private String attributeValue;
    }
}
