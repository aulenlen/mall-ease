package com.mallease.product.model.client.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 锁定库存项
 *
 * @author: Aulen
 * @create: 2026-01-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockStockItem {
    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 锁定数量
     */
    private Integer quantity;

    /**
     * 当前版本号（乐观锁）
     */
    private Integer version;
}
