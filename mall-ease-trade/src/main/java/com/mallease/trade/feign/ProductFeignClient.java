package com.mallease.trade.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SkuAvailabilityDTO;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.dto.remote.SkuStockQueryDTO;
import com.mallease.common.dto.remote.StockLockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 商品服务 Feign 客户端。
 */
@FeignClient(name = "mall-ease-product")
public interface ProductFeignClient {

    /**
     * 批量获取 SKU 简要信息。
     *
     * @param skuIds SKU ID 列表
     * @return SKU 简要信息列表
     */
    @PostMapping("/internal/sku/listSimpleByIds")
    R<List<SkuSimpleDTO>> listSkuSimpleByIds(@RequestBody List<Long> skuIds);

    /**
     * 批量查询 SKU 是否有货。
     *
     * @param queries SPU 和 SKU 查询参数
     * @return SKU 可售状态列表
     */
    @PostMapping("/internal/stock/availability")
    R<List<SkuAvailabilityDTO>> listSkuAvailability(@RequestBody List<SkuStockQueryDTO> queries);

    /**
     * 锁定库存。
     *
     * @param stockLockDTO 锁库存请求
     * @return 调用结果
     */
    @PutMapping("/internal/stock/lock")
    R<Void> lockStock(@RequestBody StockLockDTO stockLockDTO);

    /**
     * 释放订单库存。
     *
     * @param orderNos 订单号列表
     * @return 释放失败的订单号列表，空列表表示全部成功
     */
    @PutMapping("/internal/stock/unlock")
    R<List<String>> unlock(@RequestBody List<String> orderNos);

    /**
     * 支付成功后确认扣减库存。
     *
     * @param orderNo 订单号
     * @return 调用结果
     */
    @PutMapping("/internal/stock/confirm")
    R<Void> confirmStock(@RequestParam("orderNo") String orderNo);
}
