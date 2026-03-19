package com.mallease.gateway.service;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 统一维护商品详情分流的目标 URI 规则，避免路由路径散落在过滤器中。
 */
@Component
public class SpuDetailTargetUriBuilder {

    private static final String PRODUCT_SERVICE_URI = "lb://mall-ease-product";
    private static final String MARKETING_SERVICE_URI = "lb://mall-ease-marketing";

    private static final String PRODUCT_DETAIL_PATH = "/product/spu/portal/{spuId}";
    private static final String PRODUCT_FLASH_DETAIL_PATH = "/product/spu/flash-portal/{spuId}";
    private static final String MARKETING_DETAIL_PATH = "/portal/marketing/flash/detail/{spuId}";

    public String productDetailUri(Long spuId) {
        return UriComponentsBuilder.fromUriString(PRODUCT_SERVICE_URI)
                .path(PRODUCT_DETAIL_PATH)
                .buildAndExpand(spuId)
                .toUriString();
    }

    public String productFlashDetailUri(Long spuId, Long sessionId) {
        return UriComponentsBuilder.fromUriString(PRODUCT_SERVICE_URI)
                .path(PRODUCT_FLASH_DETAIL_PATH)
                .queryParam("sessionId", sessionId)
                .buildAndExpand(spuId)
                .toUriString();
    }

    public String marketingDetailUri(Long spuId, Long sessionId) {
        return UriComponentsBuilder.fromUriString(MARKETING_SERVICE_URI)
                .path(MARKETING_DETAIL_PATH)
                .queryParam("sessionId", sessionId)
                .buildAndExpand(spuId)
                .toUriString();
    }
}
