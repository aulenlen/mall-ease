package com.mallease.content.controller.internal.spurelation;

import com.mallease.common.api.R;
import com.mallease.content.service.spurelation.SpuRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "商品关联管理", description = "专题、优选专区与商品的关联管理（内部接口）")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content/spu-relation")
public class SpuRelationInternalController {

    private final SpuRelationService spuRelationService;

    @Operation(summary = "绑定商品到专题")
    @PostMapping("/subject/bind")
    public R<Integer> bindSubjects(@Parameter(description = "商品ID（SPU ID）") @RequestParam Long spuId,
                                   @Parameter(description = "专题ID列表") @RequestBody List<Long> subjectIds) {
        return R.success(spuRelationService.bindSubjectIds(spuId, subjectIds));
    }

    @Operation(summary = "绑定商品到优选专区")
    @PostMapping("/preference-area/bind")
    public R<Integer> bindPreferenceAreas(@Parameter(description = "商品ID（SPU ID）") @RequestParam Long spuId,
                                          @Parameter(description = "优选专区ID列表") @RequestBody List<Long> preferenceAreaIds) {
        return R.success(spuRelationService.bindPreferenceAreaIds(spuId, preferenceAreaIds));
    }

    @Operation(summary = "解绑商品的所有专题")
    @DeleteMapping("/subject/unbind/{spuId}")
    public R<Integer> unbindSubjects(@PathVariable Long spuId) {
        return R.success(spuRelationService.unbindSubjectIds(spuId));
    }

    @Operation(summary = "解绑商品的所有优选专区")
    @DeleteMapping("/preference-area/unbind/{spuId}")
    public R<Integer> unbindPreferenceAreas(@PathVariable Long spuId) {
        return R.success(spuRelationService.unbindPreferenceAreaIds(spuId));
    }
}
