package com.mallease.cms.controller;

import com.mallease.cms.service.CmsProductRelationService;
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
@RequestMapping("/cms/product-relation")
public class CmsProductRelationController {

    @Autowired
    private CmsProductRelationService productRelationService;

    @Operation(summary = "绑定商品到专题")
    @PostMapping("/subject/bind")
    public R<Integer> bindSubjects(
            @Parameter(description = "商品ID（SPU ID）") @RequestParam Long productId,
            @Parameter(description = "专题ID列表") @RequestBody List<Long> subjectIds) {
        int count = productRelationService.bindSubjects(productId, subjectIds);
        return R.success(count);
    }

    @Operation(summary = "绑定商品到优选专区")
    @PostMapping("/preference-area/bind")
    public R<Integer> bindPreferenceAreas(
            @Parameter(description = "商品ID（SPU ID）") @RequestParam Long productId,
            @Parameter(description = "优选专区ID列表") @RequestBody List<Long> preferenceAreaIds) {
        int count = productRelationService.bindPreferenceAreas(productId, preferenceAreaIds);
        return R.success(count);
    }

    @Operation(summary = "解绑商品的所有专题")
    @DeleteMapping("/subject/unbind/{productId}")
    public R<Integer> unbindSubjects(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        int count = productRelationService.unbindSubjects(productId);
        return R.success(count);
    }

    @Operation(summary = "解绑商品的所有优选专区")
    @DeleteMapping("/preference-area/unbind/{productId}")
    public R<Integer> unbindPreferenceAreas(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        int count = productRelationService.unbindPreferenceAreas(productId);
        return R.success(count);
    }
}