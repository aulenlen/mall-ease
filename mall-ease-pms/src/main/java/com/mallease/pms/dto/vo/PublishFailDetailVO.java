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
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;
}
