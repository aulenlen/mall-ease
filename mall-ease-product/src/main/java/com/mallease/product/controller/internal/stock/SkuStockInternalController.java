package com.mallease.product.controller.internal.stock;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.dto.remote.StockLockDTO;
import com.mallease.product.service.stock.SkuStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * SKU库存内部接口
 */
@Tag(name = "SKU库存内部接口", description = "供交易等服务调用")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/stock")
public class SkuStockInternalController {

    private final SkuStockService skuStockService;

    @Operation(summary = "锁定库存", description = "内部调用，下单时锁定库存")
    @PutMapping("/lock")
    public R<Void> lockStock(@RequestBody StockLockDTO stockLockDTO) {
        Map<Long, Integer> skuStocks = flattenSkuStocks(stockLockDTO.getSpuSkuQuantityMap());
        skuStockService.lockStock(stockLockDTO.getOrderNo(), skuStocks, stockLockDTO.getExpireTime());
        return R.success();
    }

    @Operation(summary = "释放库存", description = "内部调用，取消订单释放库存")
    @PutMapping("/unlock")
    public R<List<String>> unlock(@RequestBody List<String> orderNos) {
        return R.success(skuStockService.unlockStock(orderNos));
    }

    @Operation(summary = "确认扣减库存", description = "内部调用，支付成功后确认扣减库存")
    @PutMapping("/confirm")
    public R<Void> confirmStock(@RequestParam String orderNo) {
        skuStockService.confirmStock(orderNo);
        return R.success();
    }

    @Operation(summary = "批量查询 SKU 是否有货", description = "内部调用，购物车/下单页获取实时库存状态")
    @PostMapping("/availability")
    public R<List<SkuAvailabilityDTO>> listAvailability(@RequestBody List<SkuStockQueryDTO> queries) {
        return R.success(skuStockService.listAvailabilityBySkuIds(queries));
    }

    private Map<Long, Integer> flattenSkuStocks(Map<Long, Map<Long, Integer>> spuSkuQuantityMap) {
        if (spuSkuQuantityMap == null || spuSkuQuantityMap.isEmpty()) {
            return Map.of();
        }

        Map<Long, Integer> skuStocks = new java.util.LinkedHashMap<>();
        for (Map<Long, Integer> skuQuantityMap : spuSkuQuantityMap.values()) {
            if (skuQuantityMap == null || skuQuantityMap.isEmpty()) {
                continue;
            }
            for (Map.Entry<Long, Integer> entry : skuQuantityMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                skuStocks.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        return skuStocks;
    }
}
