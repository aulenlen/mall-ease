package com.mallease.content.pojo;

import lombok.Data;

/**
 * 专题商品关系表
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
public class ContentSubjectSpuRelation {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 专题ID
     */
    private Long subjectId;

    /**
     * 产品ID
     */
    private Long spuId;
}