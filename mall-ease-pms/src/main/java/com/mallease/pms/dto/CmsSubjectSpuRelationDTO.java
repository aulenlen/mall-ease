package com.mallease.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专题商品关联 DTO（用于服务间传输）
 *
 * @author: Aulen
 * @create: 2025-12-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmsSubjectSpuRelationDTO {
    private Long id;
    private Long subjectId;
    private Long spuId;
}
