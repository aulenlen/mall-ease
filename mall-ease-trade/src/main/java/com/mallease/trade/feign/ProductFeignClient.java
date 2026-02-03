package com.mallease.trade.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.dto.remote.StockLockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 商品服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2026-01-27
 */
@FeignClient(name = "mall-ease-product")
public interface ProductFeignClient {

    /**
     * 批量获取SKU简要信息
     *
     * @param skuIds SKU ID列表
     * @return SKU简要信息列表
     */
    @PostMapping("/product/sku/internal/listSimpleByIds")
    R<List<SkuSimpleDTO>> listSkuSimpleByIds(@RequestBody List<Long> skuIds);

    /**
     * 锁定库存
     *
     * @param stockLockDTO
     * @return
     */
    @PutMapping("/product/stock/internal/lock")
    R<Void> lockStock(@RequestBody StockLockDTO stockLockDTO);

    /**
     * 释放订单库存
     *
     * @param orderNos 订单号列表
     * @return 释放失败的订单号列表（空列表表示全部成功）
     */
    @PutMapping("/product/stock/internal/unlock")
    R<List<String>> unlock(@RequestBody List<String> orderNos);
}
