package com.mallease.pms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专题商品关系请求参数
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmsSubjectProductRelationRequest {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 专题ID
     */
    @NotNull(message = "专题ID不能为空")
    private Long subjectId;

    /**
     * 产品ID
     */
    private Long productId;
}
