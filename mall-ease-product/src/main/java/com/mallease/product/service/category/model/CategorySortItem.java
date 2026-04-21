package com.mallease.product.service.category.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类排序项（Service → Dao 数据载体）
 * <p>
 * 独立于 Controller VO，避免 Dao 依赖 Controller 包，对齐 {@code LockStockItem} 分层约定
 *
 * @author: Aulen
 * @create: 2026-04-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySortItem {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 排序值，越小越靠前
     */
    private Integer sort;
}
