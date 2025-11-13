package com.mallease.pms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优选专区商品关系请求参数
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmsPrefrenceAreaProductRelationRequest {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 优选专区ID
     */
    @NotNull(message = "优选专区ID不能为空")
    private Long prefrenceAreaId;

    /**
     * 产品ID
     */
    private Long productId;
}
