package com.mallease.search.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 属性值嵌套文档
 * 统一规格和参数，通过 type 字段区分
 *
 * @author: Aulen
 * @create: 2025-12-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsSpuAttrValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 属性ID
     */
    @Field(type = FieldType.Long)
    private Long attrId;

    /**
     * 属性名称，如 "颜色"、"内存"、"CPU型号"
     */
    @Field(type = FieldType.Keyword)
    private String attrName;

    /**
     * 属性值，如 "黑色"、"128G"、"骁龙8 Gen3"
     */
    @Field(type = FieldType.Keyword)
    private String attrValue;

    /**
     * 属性类型：0-参数 1-规格
     */
    @Field(type = FieldType.Integer)
    private Integer type;

    /**
     * 是否可筛选（规格用于筛选面板）
     */
    @Field(type = FieldType.Boolean)
    private Boolean filterable;

    /**
     * 是否可搜索
     */
    @Field(type = FieldType.Boolean)
    private Boolean searchable;
}
