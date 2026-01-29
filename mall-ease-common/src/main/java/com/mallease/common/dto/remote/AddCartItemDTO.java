package com.mallease.common.dto.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 添加购物车请求（跨服务传输）
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品数量
     */
    private Integer quantity;
}
