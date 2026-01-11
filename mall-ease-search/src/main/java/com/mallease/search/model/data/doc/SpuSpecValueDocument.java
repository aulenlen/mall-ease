package com.mallease.search.model.data.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 规格值嵌套文档
 *
 * @author: Aulen
 * @create: 2025-12-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuSpecValueDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规格ID（pms_spec.id）
     */
    @Field(type = FieldType.Long)
    private Long specId;

    /**
     * 规格名称，如 "颜色"、"内存"
     */
    @Field(type = FieldType.Keyword)
    private String specName;

    /**
     * 规格值，如 "黑色"、"128G"
     */
    @Field(type = FieldType.Keyword)
    private String specValue;

    /**
     * 展示类型：0-文字 1-颜色块 2-图片
     */
    @Field(type = FieldType.Integer)
    private Integer displayType;

    /**
     * 颜色代码（displayType=1 时使用），如 "#000000"
     */
    @Field(type = FieldType.Keyword)
    private String colorCode;

    /**
     * 图片URL（displayType=2 时使用）
     */
    @Field(type = FieldType.Keyword)
    private String image;
}