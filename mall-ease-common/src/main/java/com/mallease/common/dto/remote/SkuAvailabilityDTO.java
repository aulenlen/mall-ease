package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SKU 可售状态传输对象。
 *
 * @author: Aulen
 * @create: 2026-03-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuAvailabilityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long skuId;

    private Boolean inStock;
}
