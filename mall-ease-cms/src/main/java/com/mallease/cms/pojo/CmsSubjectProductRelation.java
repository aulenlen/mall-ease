package com.mallease.cms.pojo;

import lombok.Data;

/**
 * 专题商品关系表
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
public class CmsSubjectProductRelation {
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
    private Long productId;
}