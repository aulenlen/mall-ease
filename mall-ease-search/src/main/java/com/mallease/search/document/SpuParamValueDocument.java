package com.mallease.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 参数值嵌套文档
 *
 * @author: Aulen
 * @create: 2025-12-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpuParamValueDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数ID（pms_param.id）
     */
    @Field(type = FieldType.Long)
    private Long paramId;

    /**
     * 参数名称，如 "CPU型号"、"屏幕尺寸"
     */
    @Field(type = FieldType.Keyword)
    private String paramName;

    /**
     * 参数值，如 "骁龙8 Gen3"、"6.82英寸"
     */
    @Field(type = FieldType.Keyword)
    private String paramValue;
}