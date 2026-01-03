package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优选专区商品关联 DTO（用于服务间传输）
 *
 * @author: Aulen
 * @create: 2025-12-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentPreferenceAreaSpuRelationDTO {
    private Long id;
    private Long preferenceAreaId;
    private Long spuId;
}
