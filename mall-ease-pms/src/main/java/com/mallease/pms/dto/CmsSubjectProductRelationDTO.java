package com.mallease.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 专题商品关系DTO（服务间传输对象）
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmsSubjectProductRelationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
