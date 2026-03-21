package com.mallease.search.model.data.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * SKU 嵌套文档
 *
 * @author: Aulen
 * @create: 2025-12-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuSkuDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SKU ID
     */
    @Field(type = FieldType.Long)
    private Long skuId;

    /**
     * SKU 编码
     */
    @Field(type = FieldType.Keyword)
    private String skuCode;

    /**
     * SKU 基础成交价
     */
    @Field(type = FieldType.Double)
    private BigDecimal basePrice;
}
