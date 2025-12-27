package com.mallease.pms.feign;

import com.mallease.common.api.R;
import com.mallease.pms.dto.CmsPreferenceAreaSpuRelationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CMS 优选专区服务 Feign 客户端
 *
 * @author: Aulen
 * @create: 2025-11-14
 */
@FeignClient(name = "mall-ease-cms", contextId = "cmsPreferenceAreaFeignClient")
public interface CmsPreferenceAreaFeignClient {
    /**
     * 批量添加优选专区商品关联
     *
     * @param relationList 关联列表（DTO用于服务间传输）
     * @return 添加结果
     */
    @PostMapping("/cms/preferenceArea/spu/relation/batch")
    R<Integer> batchAddSpuRelation(@RequestBody List<CmsPreferenceAreaSpuRelationDTO> relationList);

    /**
     * 根据商品ID查询优选专区商品关联列表
     *
     * @param spuId 商品ID
     * @return 关联列表（DTO用于服务间传输）
     */
    @GetMapping("/cms/preferenceArea/spu/relation/spu/{spuId}")
    R<List<CmsPreferenceAreaSpuRelationDTO>> getRelationsBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据商品ID删除优选专区商品关联
     *
     * @param spuId 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/cms/preferenceArea/spu/relation/spu/{spuId}")
    R<Integer> deleteRelationsBySpuId(@PathVariable("spuId") Long spuId);
}
