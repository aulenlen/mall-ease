package com.mallease.content.controller;

import com.mallease.content.service.SpuRelationService;
import com.mallease.common.api.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品关联管理（专题、优选专区）
 * <p>
 * 供 PMS 模块通过 Feign 调用
 *
 * @author: Aulen
 * @create: 2025-12-20
 */
@Tag(name = "商品关联管理", description = "专题、优选专区与商品的关联管理（内部接口）")
@RestController
@RequestMapping("/content/spu-relation")
public class SpuRelationController {

    @Autowired
    private SpuRelationService spuRelationService;

    @Operation(summary = "绑定商品到专题")
    @PostMapping("/subject/bind")
    public R<Integer> bindSubjects(
            @Parameter(description = "商品ID（SPU ID）") @RequestParam Long spuId,
            @Parameter(description = "专题ID列表") @RequestBody List<Long> subjectIds) {
        int count = spuRelationService.bindSubjects(spuId, subjectIds);
        return R.success(count);
    }

    @Operation(summary = "绑定商品到优选专区")
    @PostMapping("/preference-area/bind")
    public R<Integer> bindPreferenceAreas(
            @Parameter(description = "商品ID（SPU ID）") @RequestParam Long spuId,
            @Parameter(description = "优选专区ID列表") @RequestBody List<Long> preferenceAreaIds) {
        int count = spuRelationService.bindPreferenceAreas(spuId, preferenceAreaIds);
        return R.success(count);
    }

    @Operation(summary = "解绑商品的所有专题")
    @DeleteMapping("/subject/unbind/{spuId}")
    public R<Integer> unbindSubjects(
            @Parameter(description = "商品ID") @PathVariable Long spuId) {
        int count = spuRelationService.unbindSubjects(spuId);
        return R.success(count);
    }

    @Operation(summary = "解绑商品的所有优选专区")
    @DeleteMapping("/preference-area/unbind/{spuId}")
    public R<Integer> unbindPreferenceAreas(
            @Parameter(description = "商品ID") @PathVariable Long spuId) {
        int count = spuRelationService.unbindPreferenceAreas(spuId);
        return R.success(count);
    }
}