package com.mallease.marketing.feign;

import com.mallease.common.api.R;
import com.mallease.common.dto.remote.ProductDTO;
import com.mallease.common.dto.remote.SkuSimpleDTO;
import com.mallease.common.dto.remote.SpuMatchQueryDTO;
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

    /**
     * 批量获取商品详情快照
     *
     * @param spuIds SPU ID列表
     * @return 商品详情快照列表
     */
    @PostMapping("/product/spu/internal/detailSnapshots")
    R<List<ProductDTO>> listProductDetailSnapshots(@RequestBody List<Long> spuIds);

    /**
     * 在候选SPU范围内匹配商品ID
     *
     * @param query 匹配条件
     * @return 匹配成功的SPU ID列表
     */
    @PostMapping("/product/spu/internal/matchIds")
    R<List<Long>> matchSpuIds(@RequestBody SpuMatchQueryDTO query);
}
