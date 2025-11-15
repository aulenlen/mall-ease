package com.mallease.pms.dto.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优选专区商品关系视图对象
 *
 * @author: Aulen
 * @create: 2025-11-15
 */
@Schema(description = "优选专区商品关系")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmsPreferenceAreaProductRelationVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "优选专区ID")
    private Long preferenceAreaId;

    @Schema(description = "产品ID")
    private Long productId;
}
