package com.mallease.product.model.client.cmd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解锁库存项（按 SKU 汇总后的释放单元）
 *
 * @author: Aulen
 * @create: 2026-02-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnlockStockItem {
    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 解锁数量（汇总后的总量）
     */
    private Integer quantity;
}
