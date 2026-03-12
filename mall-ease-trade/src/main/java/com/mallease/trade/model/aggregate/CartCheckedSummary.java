package com.mallease.trade.model.aggregate;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车已选中金额汇总。
 *
 * @author: Aulen
 * @create: 2026-03-11
 */
@Data
public class CartCheckedSummary {

    private BigDecimal checkedAmount;
}
