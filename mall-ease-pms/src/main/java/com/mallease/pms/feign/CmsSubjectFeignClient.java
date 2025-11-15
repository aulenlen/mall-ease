package com.mallease.pms.feign;

import com.mallease.common.api.R;
import com.mallease.pms.dto.CmsSubjectProductRelationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CMS 专题服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
@FeignClient(name = "mall-ease-cms", contextId = "cmsSubjectFeignClient")
public interface CmsSubjectFeignClient {
    /**
     * 批量添加专题商品关联
     *
     * @param relationList 关联列表（DTO用于服务间传输）
     * @return 添加结果
     */
    @PostMapping("/cms/subject/product/relation/batch")
    R<Integer> batchAddProductRelation(@RequestBody List<CmsSubjectProductRelationDTO> relationList);

    /**
     * 根据商品ID查询专题商品关联列表
     *
     * @param productId 商品ID
     * @return 关联列表（DTO用于服务间传输）
     */
    @GetMapping("/cms/subject/product/relation/product/{productId}")
    R<List<CmsSubjectProductRelationDTO>> getRelationsByProductId(@PathVariable("productId") Long productId);

    /**
     * 根据商品ID删除专题商品关联
     *
     * @param productId 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/cms/subject/product/relation/product/{productId}")
    R<Integer> deleteRelationsByProductId(@PathVariable("productId") Long productId);
}
