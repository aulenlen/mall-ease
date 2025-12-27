package com.mallease.pms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class CmsPreferenceAreaSpuRelationDTO {
    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "优选专区ID")
    private Long preferenceAreaId;
    @Schema(description = "产品ID")
    private Long spuId;
}
