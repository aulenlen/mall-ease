package com.mallease.pms.feign;

import com.mallease.common.api.R;
import com.mallease.pms.dto.CmsSubjectSpuRelationDTO;
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
    @PostMapping("/cms/subject/spu/relation/batch")
    R<Integer> batchAddSpuRelation(@RequestBody List<CmsSubjectSpuRelationDTO> relationList);

    /**
     * 根据商品ID查询专题商品关联列表
     *
     * @param spuId 商品ID
     * @return 关联列表（DTO用于服务间传输）
     */
    @GetMapping("/cms/subject/spu/relation/spu/{spuId}")
    R<List<CmsSubjectSpuRelationDTO>> getRelationsBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据商品ID删除专题商品关联
     *
     * @param spuId 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/cms/subject/spu/relation/spu/{spuId}")
    R<Integer> deleteRelationsBySpuId(@PathVariable("spuId") Long spuId);
}
