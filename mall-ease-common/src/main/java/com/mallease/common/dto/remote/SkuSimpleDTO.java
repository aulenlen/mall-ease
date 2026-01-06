package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * SKU简要信息（跨服务传输）
 *
 * @author: Aulen
 * @create: 2026-01-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuSimpleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long spuId;

    private String spuName;

    private String spuPic;

    private String skuPic;

    private BigDecimal originalPrice;

    private String specValues;
}