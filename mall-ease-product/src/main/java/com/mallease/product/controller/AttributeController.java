package com.mallease.product.controller;

import com.github.pagehelper.PageHelper;
import com.mallease.common.api.Page;
import com.mallease.common.api.PageUtils;
import com.mallease.common.api.R;
import com.mallease.product.converter.AttributeConverter;
import com.mallease.product.model.client.cmd.AttributeCmd;
import com.mallease.product.model.client.cmd.BatchUnbindAttrCmd;
import com.mallease.product.model.client.cmd.CategoryAttrRelationCmd;
import com.mallease.product.model.client.cmd.TemplateApplyCmd;
import com.mallease.product.model.client.cmd.TemplatePreviewCmd;
import com.mallease.product.model.client.query.AttributeQuery;
import com.mallease.product.model.client.vo.AttributeVO;
import com.mallease.product.model.client.vo.CategoryAttributeVO;
import com.mallease.product.model.client.vo.TemplateApplyVO;
import com.mallease.product.model.client.vo.TemplatePreviewVO;
import com.mallease.product.model.data.entity.Attribute;
import com.mallease.product.model.data.entity.CategoryAttributeRelation;
import com.mallease.product.service.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 商品属性控制器（全局属性池）
 *
 * @author: Aulen
 * @create: 2026-01-11
 */
@Tag(name = "商品属性管理")
@RestController
@RequestMapping("/product/attribute")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;
    private final AttributeConverter attributeConverter;

    // 属性池管理（全局）

    @Operation(summary = "创建全局属性")
    @PostMapping("/create")
    public R<Long> create(@Validated(AttributeCmd.Create.class) @RequestBody AttributeCmd cmd) {
        Attribute entity = attributeConverter.cmdToEntity(cmd);
        return R.success(attributeService.create(entity));
    }

    @Operation(summary = "更新属性")
    @PostMapping("/update")
    public R<Integer> update(@Validated(AttributeCmd.Update.class) @RequestBody AttributeCmd cmd) {
        Attribute entity = attributeConverter.cmdToEntityForUpdate(cmd);
        return R.success(attributeService.update(entity));
    }

    @Operation(summary = "删除属性")
    @PostMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable Long id) {
        return R.success(attributeService.delete(id));
    }

    @Operation(summary = "批量删除属性")
    @PostMapping("/delete/batch")
    public R<Integer> deleteBatch(@RequestBody List<Long> ids) {
        return R.success(attributeService.deleteBatch(ids));
    }

    @Operation(summary = "获取属性详情")
    @GetMapping("/{id}")
    public R<AttributeVO> getById(@PathVariable Long id) {
        Attribute entity = attributeService.getById(id);
        return R.success(attributeConverter.entityToVo(entity));
    }

    @Operation(summary = "查询属性池列表", description = "支持分页、条件查询")
    @GetMapping("/list")
    public R<Page<AttributeVO>> list(@Validated @ModelAttribute AttributeQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Attribute> entities = attributeService.list(query);
        List<AttributeVO> voList = attributeConverter.entityListToVoList(entities);
        Page<AttributeVO> result = PageUtils.buildPage(entities, voList);
        return R.success(result);
    }

    // 分类关联属性

    @Operation(summary = "为分类关联属性")
    @PostMapping("/bindCategory")
    public R<Integer> bindCategory(@Validated @RequestBody CategoryAttrRelationCmd cmd) {
        CategoryAttributeRelation entity = attributeConverter.relationCmdToEntity(cmd);
        return R.success(attributeService.bindCategory(entity));
    }

    @Operation(summary = "批量为分类关联属性")
    @PostMapping("/bindCategory/batch/{categoryId}")
    public R<Integer> bindCategoryBatch(@PathVariable Long categoryId, @RequestBody List<CategoryAttrRelationCmd> cmdList) {
        List<CategoryAttributeRelation> entities = attributeConverter.relationCmdListToEntityList(categoryId, cmdList);
        return R.success(attributeService.bindCategoryBatch(entities));
    }

    @Operation(summary = "解除分类与属性的关联")
    @PostMapping("/unbindCategory")
    public R<Integer> unbindCategory(@RequestParam Long categoryId, @RequestParam Long attrId) {
        return R.success(attributeService.unbindCategory(categoryId, attrId));
    }

    @Operation(summary = "更新分类-属性关联信息")
    @PostMapping("/updateRelation")
    public R<Integer> updateRelation(@Validated @RequestBody CategoryAttrRelationCmd cmd) {
        CategoryAttributeRelation entity = attributeConverter.relationCmdToEntity(cmd);
        return R.success(attributeService.updateRelation(entity));
    }

    @Operation(summary = "批量更新分类-属性关联信息")
    @PostMapping("/updateRelation/batch/{categoryId}")
    public R<Integer> updateRelationBatch(@PathVariable Long categoryId, @RequestBody List<CategoryAttrRelationCmd> cmdList) {
        List<CategoryAttributeRelation> entities = attributeConverter.relationCmdListToEntityList(categoryId, cmdList);
        return R.success(attributeService.updateRelationBatch(entities));
    }

    @Operation(summary = "查询分类已关联的属性")
    @GetMapping("/listByCategory/{categoryId}")
    public R<List<CategoryAttributeVO>> listByCategory(@PathVariable Long categoryId) {
        List<CategoryAttributeRelation> relations = attributeService.listByCategory(categoryId);
        return R.success(buildCategoryAttributeVOList(relations));
    }

    @Operation(summary = "查询分类已关联的规格属性")
    @GetMapping("/listSpecsByCategory/{categoryId}")
    public R<List<CategoryAttributeVO>> listSpecsByCategory(@PathVariable Long categoryId) {
        List<CategoryAttributeRelation> relations = attributeService.listSpecsByCategory(categoryId);
        return R.success(buildCategoryAttributeVOList(relations));
    }

    @Operation(summary = "查询分类已关联的参数属性")
    @GetMapping("/listParamsByCategory/{categoryId}")
    public R<List<CategoryAttributeVO>> listParamsByCategory(@PathVariable Long categoryId) {
        List<CategoryAttributeRelation> relations = attributeService.listParamsByCategory(categoryId);
        return R.success(buildCategoryAttributeVOList(relations));
    }

    @Operation(summary = "查询分类未关联的属性（供勾选弹窗）", description = "支持分页、条件查询")
    @GetMapping("/listUnbind/{categoryId}")
    public R<Page<AttributeVO>> listUnbindByCategory(@PathVariable Long categoryId, @ModelAttribute AttributeQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Attribute> entities = attributeService.listUnbindByCategory(categoryId, query);
        List<AttributeVO> voList = attributeConverter.entityListToVoList(entities);
        Page<AttributeVO> result = PageUtils.buildPage(entities, voList);
        return R.success(result);
    }

    @Operation(summary = "从父分类复制属性关联")
    @PostMapping("/copyFromParent")
    public R<Integer> copyFromParent(@RequestParam Long parentCategoryId, @RequestParam Long childCategoryId) {
        return R.success(attributeService.copyFromParent(parentCategoryId, childCategoryId));
    }

    @Operation(summary = "批量解绑属性")
    @PostMapping("/unbindCategory/batch")
    public R<Integer> unbindCategoryBatch(@Validated @RequestBody BatchUnbindAttrCmd cmd) {
        return R.success(attributeService.unbindCategoryBatch(cmd.getCategoryId(), cmd.getAttrIds()));
    }

    @Operation(summary = "模板预览", description = "预览从模板分类复制属性的影响")
    @PostMapping("/template/preview")
    public R<TemplatePreviewVO> templatePreview(@Validated @RequestBody TemplatePreviewCmd cmd) {
        return R.success(attributeService.templatePreview(
                cmd.getTemplateCategoryId(),
                cmd.getTargetCategoryId(),
                cmd.getMode(),
                cmd.getScope()));
    }

    @Operation(summary = "模板应用", description = "执行模板复制（支持 replace/merge 模式）")
    @PostMapping("/template/apply")
    public R<TemplateApplyVO> templateApply(@Validated @RequestBody TemplateApplyCmd cmd) {
        return R.success(attributeService.templateApply(
                cmd.getTemplateCategoryId(),
                cmd.getTargetCategoryId(),
                cmd.getMode(),
                cmd.getScope(),
                cmd.getTraceId(),
                cmd.getSelectedAddAttrIds()));
    }

    // 属性选项

    @Operation(summary = "获取属性选项（优先关联表选项，其次全局选项）")
    @GetMapping("/options")
    public R<List<String>> getAttrOptions(@RequestParam Long categoryId, @RequestParam Long attrId) {
        return R.success(attributeService.getAttrOptions(categoryId, attrId));
    }

    // 私有方法

    /**
     * 构建分类属性视图对象列表
     */
    private List<CategoryAttributeVO> buildCategoryAttributeVOList(List<CategoryAttributeRelation> relations) {
        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }
        List<Long> attrIds = relations.stream().map(CategoryAttributeRelation::getAttrId).toList();
        List<Attribute> attributes = attributeService.listByIds(attrIds);
        return attributeConverter.buildCategoryAttributeVOList(relations, attributes);
    }
}