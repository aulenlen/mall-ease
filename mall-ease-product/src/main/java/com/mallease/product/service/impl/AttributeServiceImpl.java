package com.mallease.product.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.product.dao.AttributeDao;
import com.mallease.product.dao.AttributeValueDao;
import com.mallease.product.dao.CategoryAttributeRelationDao;
import com.mallease.product.model.client.query.AttributeQuery;
import com.mallease.product.model.client.vo.TemplateApplyVO;
import com.mallease.product.model.client.vo.TemplatePreviewVO;
import com.mallease.product.model.data.entity.Attribute;
import com.mallease.product.model.data.entity.AttributeValue;
import com.mallease.product.model.data.entity.CategoryAttributeRelation;
import com.mallease.product.service.AttributeService;
import com.mallease.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品属性服务实现（全局属性池）
 *
 * @author: Aulen
 * @create: 2026-01-11
 */
@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeDao attributeDao;
    private final AttributeValueDao attributeValueDao;
    private final CategoryAttributeRelationDao relationDao;
    private final CategoryService categoryService;

    // 属性池管理

    @Override
    public Long create(Attribute entity) {
        Attribute existing = attributeDao.selectByName(entity.getName());
        if (existing != null) {
            throw new ApiException("已存在同名属性");
        }
        attributeDao.insert(entity);
        return entity.getId();
    }

    @Override
    public int update(Attribute entity) {
        Attribute existing = attributeDao.selectById(entity.getId());
        if (existing == null) {
            throw new ApiException("属性不存在");
        }

        Attribute sameNameAttr = attributeDao.selectByName(entity.getName());
        if (sameNameAttr != null && !sameNameAttr.getId().equals(entity.getId())) {
            throw new ApiException("已存在同名属性");
        }

        return attributeDao.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        relationDao.deleteByAttrId(id);
        return attributeDao.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        // 批量删除所有分类关联（单次数据库调用）
        relationDao.deleteByAttrIds(ids);
        return attributeDao.deleteBatch(ids);
    }

    @Override
    public Attribute getById(Long id) {
        Attribute entity = attributeDao.selectById(id);
        if (entity == null) {
            throw new ApiException("属性不存在");
        }
        return entity;
    }

    @Override
    public List<Attribute> list() {
        return attributeDao.selectAll();
    }

    @Override
    public List<Attribute> list(AttributeQuery query) {
        return attributeDao.selectByQuery(query);
    }

    @Override
    public List<Attribute> listByKeyword(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return list();
        }
        return attributeDao.selectByNameLike(keyword);
    }

    @Override
    public List<Attribute> listByType(Integer type) {
        return attributeDao.selectByType(type);
    }

    @Override
    public List<Attribute> listSearchable() {
        return attributeDao.selectSearchable();
    }

    @Override
    public List<Attribute> listFilterable() {
        return attributeDao.selectFilterable();
    }

    @Override
    public List<Attribute> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return attributeDao.selectByIds(ids);
    }

    // 分类关联属性

    @Override
    public int bindCategory(CategoryAttributeRelation entity) {
        CategoryAttributeRelation existing = relationDao.selectByCategoryIdAndAttrId(
                entity.getCategoryId(), entity.getAttrId());
        if (existing != null) {
            throw new ApiException("该分类已关联此属性");
        }
        return relationDao.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindCategoryBatch(List<CategoryAttributeRelation> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        return relationDao.insertBatch(entities);
    }

    @Override
    public int unbindCategory(Long categoryId, Long attrId) {
        return relationDao.deleteByCategoryIdAndAttrId(categoryId, attrId);
    }

    @Override
    public int updateRelation(CategoryAttributeRelation entity) {
        CategoryAttributeRelation existing = relationDao.selectByCategoryIdAndAttrId(
                entity.getCategoryId(), entity.getAttrId());
        if (existing == null) {
            throw new ApiException("关联关系不存在");
        }

        // 保留原有 ID
        entity.setId(existing.getId());
        return relationDao.updateById(entity);
    }

    @Override
    public int updateRelationBatch(List<CategoryAttributeRelation> entities) {
        if (CollUtil.isEmpty(entities)) {
            return 0;
        }
        return relationDao.updateBatch(entities);
    }

    @Override
    public List<CategoryAttributeRelation> listByCategory(Long categoryId) {
        return relationDao.selectByCategoryId(categoryId);
    }

    @Override
    public List<CategoryAttributeRelation> listSpecsByCategory(Long categoryId) {
        return relationDao.selectByCategoryIdAndType(categoryId, 1);
    }

    @Override
    public List<CategoryAttributeRelation> listParamsByCategory(Long categoryId) {
        return relationDao.selectByCategoryIdAndType(categoryId, 0);
    }

    @Override
    public List<Attribute> listUnbindByCategory(Long categoryId) {
        List<Long> unbindAttrIds = relationDao.selectUnbindAttrIds(categoryId);
        if (CollectionUtils.isEmpty(unbindAttrIds)) {
            return Collections.emptyList();
        }
        return attributeDao.selectByIds(unbindAttrIds);
    }

    @Override
    public List<Attribute> listUnbindByCategory(Long categoryId, AttributeQuery query) {
        return attributeDao.selectUnbindByCategory(categoryId, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int copyFromParent(Long parentCategoryId, Long childCategoryId) {
        List<CategoryAttributeRelation> parentRelations = relationDao.selectByCategoryId(parentCategoryId);
        if (CollectionUtils.isEmpty(parentRelations)) {
            return 0;
        }

        List<CategoryAttributeRelation> childRelations = parentRelations.stream()
                .map(parent -> {
                    CategoryAttributeRelation child = new CategoryAttributeRelation();
                    child.setCategoryId(childCategoryId);
                    child.setAttrId(parent.getAttrId());
                    child.setGroupName(parent.getGroupName());
                    child.setSort(parent.getSort());
                    child.setRequired(parent.getRequired());
                    child.setOptions(parent.getOptions());
                    return child;
                })
                .toList();

        return relationDao.insertBatch(childRelations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unbindCategoryBatch(Long categoryId, List<Long> attrIds) {
        if (CollectionUtils.isEmpty(attrIds)) {
            return 0;
        }
        return relationDao.deleteByCategoryIdAndAttrIds(categoryId, attrIds);
    }

    @Override
    public TemplatePreviewVO templatePreview(Long templateCategoryId, Long targetCategoryId, String mode, String scope) {

        validateLeafCategory(targetCategoryId);

        List<CategoryAttributeRelation> templateRelations = getRelationsByScope(templateCategoryId, scope);

        List<CategoryAttributeRelation> targetRelations = getRelationsByScope(targetCategoryId, scope);
        Set<Long> targetAttrIds = targetRelations.stream()
                .map(CategoryAttributeRelation::getAttrId)
                .collect(Collectors.toSet());

        List<CategoryAttributeRelation> toAdd = new ArrayList<>();
        List<CategoryAttributeRelation> toSkip = new ArrayList<>();

        for (CategoryAttributeRelation templateRel : templateRelations) {
            if (targetAttrIds.contains(templateRel.getAttrId())) {
                toSkip.add(templateRel);  // 已存在，跳过
            } else {
                toAdd.add(templateRel);   // 新增
            }
        }

        List<CategoryAttributeRelation> toRemove = new ArrayList<>();
        if ("replace".equals(mode)) {
            Set<Long> templateAttrIds = templateRelations.stream()
                    .map(CategoryAttributeRelation::getAttrId)
                    .collect(Collectors.toSet());
            toRemove = targetRelations.stream()
                    .filter(r -> !templateAttrIds.contains(r.getAttrId()))
                    .toList();
        }

        Set<Long> allAttrIds = new HashSet<>();
        toAdd.forEach(r -> allAttrIds.add(r.getAttrId()));
        toRemove.forEach(r -> allAttrIds.add(r.getAttrId()));
        toSkip.forEach(r -> allAttrIds.add(r.getAttrId()));

        Map<Long, Attribute> attrMap = Collections.emptyMap();
        if (!allAttrIds.isEmpty()) {
            attrMap = attributeDao.selectByIds(new ArrayList<>(allAttrIds)).stream()
                    .collect(Collectors.toMap(Attribute::getId, a -> a));
        }

        TemplatePreviewVO vo = new TemplatePreviewVO();
        vo.setTraceId(IdUtil.fastSimpleUUID());

        vo.setAddAttrIds(toAdd.stream().map(CategoryAttributeRelation::getAttrId).toList());

        TemplatePreviewVO.TemplateSummary summary = new TemplatePreviewVO.TemplateSummary();
        summary.setAddCount(toAdd.size());
        summary.setRemoveCount(toRemove.size());
        summary.setSkipCount(toSkip.size());
        vo.setSummary(summary);

        vo.setSampleAddList(buildSampleList(toAdd, attrMap, 10));
        vo.setSampleRemoveList(buildSampleList(toRemove, attrMap, 10));
        vo.setSampleSkipList(buildSampleList(toSkip, attrMap, 10));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateApplyVO templateApply(Long templateCategoryId, Long targetCategoryId,
                                          String mode, String scope, String traceId,
                                          List<Long> selectedAddAttrIds) {
        validateLeafCategory(targetCategoryId);

        List<CategoryAttributeRelation> templateRelations = getRelationsByScope(templateCategoryId, scope);

        List<CategoryAttributeRelation> targetRelations = getRelationsByScope(targetCategoryId, scope);
        Set<Long> targetAttrIds = targetRelations.stream()
                .map(CategoryAttributeRelation::getAttrId)
                .collect(Collectors.toSet());

        int addCount = 0;
        int removeCount = 0;
        int skipCount = 0;

        if ("replace".equals(mode)) {
            Set<Long> templateAttrIds = templateRelations.stream()
                    .map(CategoryAttributeRelation::getAttrId)
                    .collect(Collectors.toSet());

            List<Long> toDeleteAttrIds = targetRelations.stream()
                    .map(CategoryAttributeRelation::getAttrId)
                    .filter(attrId -> !templateAttrIds.contains(attrId))
                    .toList();

            if (!toDeleteAttrIds.isEmpty()) {
                removeCount = relationDao.deleteByCategoryIdAndAttrIds(targetCategoryId, toDeleteAttrIds);
            }
        }

        List<CategoryAttributeRelation> toInsert = new ArrayList<>();
        for (CategoryAttributeRelation templateRel : templateRelations) {
            if (targetAttrIds.contains(templateRel.getAttrId())) {
                skipCount++;
            } else {
                if (CollUtil.isNotEmpty(selectedAddAttrIds) && !selectedAddAttrIds.contains(templateRel.getAttrId())) {
                    skipCount++;
                    continue;
                }

                CategoryAttributeRelation newRel = new CategoryAttributeRelation();
                newRel.setCategoryId(targetCategoryId);
                newRel.setAttrId(templateRel.getAttrId());
                newRel.setGroupName(templateRel.getGroupName());
                newRel.setSort(templateRel.getSort());
                newRel.setRequired(templateRel.getRequired());
                newRel.setOptions(templateRel.getOptions());
                toInsert.add(newRel);
            }
        }

        if (!toInsert.isEmpty()) {
            addCount = relationDao.insertBatch(toInsert);
        }

        TemplateApplyVO vo = new TemplateApplyVO();
        vo.setTraceId(traceId);
        vo.setSuccess(true);

        TemplatePreviewVO.TemplateSummary summary = new TemplatePreviewVO.TemplateSummary();
        summary.setAddCount(addCount);
        summary.setRemoveCount(removeCount);
        summary.setSkipCount(skipCount);
        vo.setSummary(summary);

        return vo;
    }

    /**
     * 校验目标必须是叶子分类
     */
    private void validateLeafCategory(Long categoryId) {
        Map<Long, Long> childCountMap = categoryService.countChildrenByParentIds(List.of(categoryId));
        if (childCountMap.getOrDefault(categoryId, 0L) > 0) {
            throw new ApiException("目标必须是叶子分类");
        }
    }

    /**
     * 根据 scope 获取分类属性关联
     */
    private List<CategoryAttributeRelation> getRelationsByScope(Long categoryId, String scope) {
        return switch (scope) {
            case "spec" -> relationDao.selectByCategoryIdAndType(categoryId, 1);
            case "param" -> relationDao.selectByCategoryIdAndType(categoryId, 0);
            case "both" -> relationDao.selectByCategoryId(categoryId);
            default -> throw new ApiException("无效的 scope: " + scope);
        };
    }

    /**
     * 构建属性样例列表
     */
    private List<TemplatePreviewVO.TemplateAttrSample> buildSampleList(
            List<CategoryAttributeRelation> relations,
            Map<Long, Attribute> attrMap,
            int limit) {
        return relations.stream()
                .limit(limit)
                .map(r -> {
                    Attribute attr = attrMap.get(r.getAttrId());
                    TemplatePreviewVO.TemplateAttrSample sample = new TemplatePreviewVO.TemplateAttrSample();
                    sample.setAttrId(r.getAttrId());
                    sample.setAttrName(attr != null ? attr.getName() : "未知");
                    sample.setType(attr != null ? attr.getType() : null);
                    sample.setGroupName(r.getGroupName());
                    return sample;
                })
                .toList();
    }

    // 属性值相关

    @Override
    public List<AttributeValue> listParamValuesBySpuIds(List<Long> spuIds) {
        if (CollectionUtils.isEmpty(spuIds)) {
            return List.of();
        }
        return attributeValueDao.selectParamsBySpuIds(spuIds);
    }

    @Override
    public List<String> getAttrOptions(Long categoryId, Long attrId) {
        CategoryAttributeRelation relation = relationDao.selectByCategoryIdAndAttrId(categoryId, attrId);

        if (relation != null && StrUtil.isNotBlank(relation.getOptions())) {
            return JSONUtil.toList(relation.getOptions(), String.class);
        }

        Attribute attr = attributeDao.selectById(attrId);
        if (attr != null && StrUtil.isNotBlank(attr.getOptions())) {
            return JSONUtil.toList(attr.getOptions(), String.class);
        }

        return Collections.emptyList();
    }
}