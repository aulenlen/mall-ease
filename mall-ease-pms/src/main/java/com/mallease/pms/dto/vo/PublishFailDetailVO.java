package com.mallease.pms.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Aulen
 * @description: 商品上架失败详情
 * @create: 2025-11-19
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublishFailDetailVO {
    /**
     * 失败原因
     */
    private String reason;

    /**
     * 商品ID
     */
    @Deprecated
    private Long productId;

    /**
     *  SPUID
     */
    private Long spuId;

    /**
     * SPU名称
     */
    private String spuName;

    /**
     * 商品名称
     */
    @Deprecated
    private String productName;
}
