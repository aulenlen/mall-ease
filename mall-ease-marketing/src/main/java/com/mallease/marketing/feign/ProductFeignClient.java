package com.mallease.marketing.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 商品服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2026-01-06
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
}