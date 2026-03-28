package com.mallease.product.controller.admin.attribute;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.product.controller.admin.attribute.vo.AttributePageReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeRelationBatchUnbindReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeRespVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeSaveReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplateApplyReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplateApplyRespVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplatePreviewReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplatePreviewRespVO;
import com.mallease.product.controller.admin.attribute.vo.CategoryAttributeRelationRespVO;
import com.mallease.product.controller.admin.attribute.vo.CategoryAttributeRelationSaveReqVO;
import com.mallease.product.convert.attribute.AttributeConvert;
import com.mallease.product.dal.entity.Attribute;
import com.mallease.product.dal.entity.CategoryAttributeRelation;
import com.mallease.product.service.attribute.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 后台商品属性管理
 */
@Tag(name = "后台商品属性管理")
@RestController
@RequestMapping("/admin/catalog/attributes")
@RequiredArgsConstructor
public class AttributeAdminController {

    private final AttributeService attributeService;
    private final AttributeConvert attributeConvert;

    @Operation(summary = "创建全局属性")
    @PostMapping
    public R<Long> create(@Validated(AttributeSaveReqVO.Create.class) @RequestBody AttributeSaveReqVO reqVO) {
        return R.success(attributeService.create(reqVO));
    }

    @Operation(summary = "更新属性")
    @PutMapping
    public R<Integer> update(@Validated(AttributeSaveReqVO.Update.class) @RequestBody AttributeSaveReqVO reqVO) {
        return R.success(attributeService.update(reqVO));
    }

    @Operation(summary = "删除属性")
    @DeleteMapping("/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        return R.success(attributeService.delete(id));
    }

    @Operation(summary = "批量删除属性")
    @DeleteMapping("/batch")
    public R<Integer> deleteBatch(@RequestBody List<Long> ids) {
        return R.success(attributeService.deleteBatch(ids));
    }

    @Operation(summary = "获取属性详情")
    @GetMapping("/{id}")
    public R<AttributeRespVO> get(@PathVariable Long id) {
        return R.success(attributeConvert.toAttributeResp(attributeService.get(id)));
    }

    @Operation(summary = "查询属性池列表", description = "支持分页、条件查询")
    @GetMapping
    public R<Page<AttributeRespVO>> page(@Validated @ModelAttribute AttributePageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Attribute> entities = attributeService.page(reqVO);
        return R.success(PageUtils.convertPage(entities, attributeConvert::toAttributeRespList));
    }

    @Operation(summary = "为分类关联属性")
    @PostMapping("/category-relations")
    public R<Integer> bindCategory(@Validated @RequestBody CategoryAttributeRelationSaveReqVO reqVO) {
        return R.success(attributeService.bindCategory(reqVO));
    }

    @Operation(summary = "批量为分类关联属性")
    @PostMapping("/category-relations/batch/{categoryId}")
    public R<Integer> bindCategoryBatch(@PathVariable Long categoryId,
                                        @RequestBody List<CategoryAttributeRelationSaveReqVO> reqVOList) {
        return R.success(attributeService.bindCategoryBatch(categoryId, reqVOList));
    }

    @Operation(summary = "解除分类与属性的关联")
    @DeleteMapping("/category-relations")
    public R<Integer> unbindCategory(@RequestParam Long categoryId, @RequestParam Long attrId) {
        return R.success(attributeService.unbindCategory(categoryId, attrId));
    }

    @Operation(summary = "更新分类-属性关联信息")
    @PutMapping("/category-relations")
    public R<Integer> updateRelation(@Validated @RequestBody CategoryAttributeRelationSaveReqVO reqVO) {
        return R.success(attributeService.updateRelation(reqVO));
    }

    @Operation(summary = "批量更新分类-属性关联信息")
    @PutMapping("/category-relations/batch/{categoryId}")
    public R<Integer> updateRelationBatch(@PathVariable Long categoryId,
                                          @RequestBody List<CategoryAttributeRelationSaveReqVO> reqVOList) {
        return R.success(attributeService.updateRelationBatch(categoryId, reqVOList));
    }

    @Operation(summary = "查询分类已关联的属性")
    @GetMapping("/category-relations/categories/{categoryId}")
    public R<List<CategoryAttributeRelationRespVO>> listByCategory(@PathVariable Long categoryId) {
        return R.success(buildCategoryAttributeRelationRespList(attributeService.listByCategory(categoryId)));
    }

    @Operation(summary = "查询分类已关联的规格属性")
    @GetMapping("/category-relations/categories/{categoryId}/specs")
    public R<List<CategoryAttributeRelationRespVO>> listSpecsByCategory(@PathVariable Long categoryId) {
        return R.success(buildCategoryAttributeRelationRespList(attributeService.listSpecsByCategory(categoryId)));
    }

    @Operation(summary = "查询分类已关联的参数属性")
    @GetMapping("/category-relations/categories/{categoryId}/params")
    public R<List<CategoryAttributeRelationRespVO>> listParamsByCategory(@PathVariable Long categoryId) {
        return R.success(buildCategoryAttributeRelationRespList(attributeService.listParamsByCategory(categoryId)));
    }

    @Operation(summary = "查询分类未关联的属性（供勾选弹窗）", description = "支持分页、条件查询")
    @GetMapping("/unbound/categories/{categoryId}")
    public R<Page<AttributeRespVO>> listUnbindByCategory(@PathVariable Long categoryId,
                                                         @ModelAttribute AttributePageReqVO reqVO) {
        PageHelper.startPage(reqVO.getPageNum(), reqVO.getPageSize());
        List<Attribute> entities = attributeService.listUnbindByCategory(categoryId, reqVO);
        return R.success(PageUtils.convertPage(entities, attributeConvert::toAttributeRespList));
    }

    @Operation(summary = "从父分类复制属性关联")
    @PostMapping("/copy-from-parent")
    public R<Integer> copyFromParent(@RequestParam Long parentCategoryId, @RequestParam Long childCategoryId) {
        return R.success(attributeService.copyFromParent(parentCategoryId, childCategoryId));
    }

    @Operation(summary = "批量解绑属性")
    @DeleteMapping("/category-relations/batch")
    public R<Integer> unbindCategoryBatch(@Validated @RequestBody AttributeRelationBatchUnbindReqVO reqVO) {
        return R.success(attributeService.unbindCategoryBatch(reqVO));
    }

    @Operation(summary = "模板预览", description = "预览从模板分类复制属性的影响")
    @PostMapping("/template-preview")
    public R<AttributeTemplatePreviewRespVO> templatePreview(@Validated @RequestBody AttributeTemplatePreviewReqVO reqVO) {
        return R.success(attributeService.templatePreview(reqVO));
    }

    @Operation(summary = "模板应用", description = "执行模板复制（支持 replace/merge 模式）")
    @PostMapping("/template-apply")
    public R<AttributeTemplateApplyRespVO> templateApply(@Validated @RequestBody AttributeTemplateApplyReqVO reqVO) {
        return R.success(attributeService.templateApply(reqVO));
    }

    @Operation(summary = "获取属性选项（优先关联表选项，其次全局选项）")
    @GetMapping("/options")
    public R<List<String>> getAttrOptions(@RequestParam Long categoryId, @RequestParam Long attrId) {
        return R.success(attributeService.getAttrOptions(categoryId, attrId));
    }

    private List<CategoryAttributeRelationRespVO> buildCategoryAttributeRelationRespList(List<CategoryAttributeRelation> relations) {
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        List<Long> attrIds = relations.stream().map(CategoryAttributeRelation::getAttrId).toList();
        List<Attribute> attributes = attributeService.listByIds(attrIds);
        return attributeConvert.buildCategoryAttributeRelationRespList(relations, attributes);
    }
}