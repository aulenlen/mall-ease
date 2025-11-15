package com.mallease.pms.feign;

import com.mallease.common.api.R;
import com.mallease.pms.dto.request.CmsPrefrenceAreaProductRelationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CMS 优选专区服务 Feign 客户端
 *
 * @author: Claude
 * @create: 2025-11-14
 */
@FeignClient(name = "mall-ease-cms", contextId = "cmsPrefrenceAreaFeignClient")
public interface CmsPrefrenceAreaFeignClient {
    /**
     * 批量添加优选专区商品关联
     *
     * @param relationList 关联列表
     * @return 添加结果
     */
    @PostMapping("/cms/prefrenceArea/product/relation/batch")
    R<Integer> batchAddProductRelation(@RequestBody List<CmsPrefrenceAreaProductRelationRequest> relationList);

    /**
     * 根据商品ID查询优选专区商品关联列表
     *
     * @param productId 商品ID
     * @return 关联列表
     */
    @GetMapping("/cms/prefrenceArea/product/relation/product/{productId}")
    R<List<CmsPrefrenceAreaProductRelationRequest>> getRelationsByProductId(@PathVariable("productId") Long productId);

    /**
     * 根据商品ID删除优选专区商品关联
     *
     * @param productId 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/cms/prefrenceArea/product/relation/product/{productId}")
    R<Integer> deleteRelationsByProductId(@PathVariable("productId") Long productId);
}
