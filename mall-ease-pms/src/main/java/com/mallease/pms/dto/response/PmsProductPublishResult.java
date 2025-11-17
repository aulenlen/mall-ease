package com.mallease.pms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: Aulen
 * @description: 商品批量上架结果
 * @create: 2025-11-17 01:18
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsProductPublishResult {
    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failCount;

    /**
     * 失败详情列表
     */
    private List<PublishFailDetail> failDetails;

    /**
     * 商品上架失败详情
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PublishFailDetail {
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
}
