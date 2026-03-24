package com.mallease.product.service.attribute;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mallease.common.exception.ApiException;
import com.mallease.product.controller.admin.attribute.vo.AttributePageReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeRelationBatchUnbindReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeSaveReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplateApplyReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplateApplyRespVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplatePreviewReqVO;
import com.mallease.product.controller.admin.attribute.vo.AttributeTemplatePreviewRespVO;
import com.mallease.product.controller.admin.attribute.vo.CategoryAttributeRelationSaveReqVO;
import com.mallease.product.convert.attribute.AttributeConvert;
import com.mallease.product.dal.mapper.AttributeDao;
import com.mallease.product.dal.mapper.AttributeValueDao;
import com.mallease.product.dal.mapper.CategoryAttributeRelationDao;
import com.mallease.product.dal.entity.Attribute;
import com.mallease.product.dal.entity.AttributeValue;
import com.mallease.product.dal.entity.CategoryAttributeRelation;
import com.mallease.product.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品属性服务实现
 */
@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeDao attributeDao;
    private final AttributeValueDao attributeValueDao;
    private final CategoryAttributeRelationDao relationDao;
    private final CategoryService categoryService;
    private final AttributeConvert attributeConvert;

    @Override
    public Long create(AttributeSaveReqVO reqVO) {
        Attribute existing = attributeDao.selectByName(reqVO.getName());
        if (existing != null) {
            throw new ApiException("已存在同名属性");
        }
        Attribute entity = attributeConvert.reqVOToEntity(reqVO);
        attributeDao.insert(entity);
        return entity.getId();
    }

    @Override
    public int update(AttributeSaveReqVO reqVO) {
        Attribute existing = attributeDao.selectById(reqVO.getId());
        if (existing == null) {
            throw new ApiException("属性不存在");
        }

        Attribute sameNameAttr = attributeDao.selectByName(reqVO.getName());
        if (sameNameAttr != null && !sameNameAttr.getId().equals(reqVO.getId())) {
            throw new ApiException("已存在同名属性");
        }

        Attribute entity = attributeConvert.reqVOToEntity(reqVO);
        entity.setId(reqVO.getId());
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
        relationDao.deleteByAttrIds(ids);
        return attributeDao.deleteBatch(ids);
    }

    @Override
    public Attribute get(Long id) {
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
    public List<Attribute> page(AttributePageReqVO reqVO) {
        return attributeDao.selectByQuery(reqVO);
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

    @Override
    public int bindCategory(CategoryAttributeRelationSaveReqVO reqVO) {
        CategoryAttributeRelation existing = relationDao.selectByCategoryIdAndAttrId(reqVO.getCategoryId(), reqVO.getAttrId());
        if (existing != null) {
            throw new ApiException("该分类已关联此属性");
        }
        CategoryAttributeRelation entity = attributeConvert.relationReqVOToEntity(reqVO);
        return relationDao.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindCategoryBatch(Long categoryId, List<CategoryAttributeRelationSaveReqVO> reqVOList) {
        if (CollectionUtils.isEmpty(reqVOList)) {
            return 0;
        }
        return relationDao.insertBatch(attributeConvert.relationReqVOListToEntityList(categoryId, reqVOList));
    }

    @Override
    public int unbindCategory(Long categoryId, Long attrId) {
        return relationDao.deleteByCategoryIdAndAttrId(categoryId, attrId);
    }

    @Override
    public int updateRelation(CategoryAttributeRelationSaveReqVO reqVO) {
        CategoryAttributeRelation existing = relationDao.selectByCategoryIdAndAttrId(reqVO.getCategoryId(), reqVO.getAttrId());
        if (existing == null) {
            throw new ApiException("关联关系不存在");
        }
        CategoryAttributeRelation entity = attributeConvert.relationReqVOToEntity(reqVO);
        entity.setId(existing.getId());
        return relationDao.updateById(entity);
    }

    @Override
    public int updateRelationBatch(Long categoryId, List<CategoryAttributeRelationSaveReqVO> reqVOList) {
        if (CollUtil.isEmpty(reqVOList)) {
            return 0;
        }
        return relationDao.updateBatch(attributeConvert.relationReqVOListToEntityList(categoryId, reqVOList));
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
    public List<Attribute> listUnbindByCategory(Long categoryId, AttributePageReqVO reqVO) {
        return attributeDao.selectUnbindByCategory(categoryId, reqVO);
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
    public int unbindCategoryBatch(AttributeRelationBatchUnbindReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getAttrIds())) {
            return 0;
        }
        return relationDao.deleteByCategoryIdAndAttrIds(reqVO.getCategoryId(), reqVO.getAttrIds());
    }

    @Override
    public AttributeTemplatePreviewRespVO templatePreview(AttributeTemplatePreviewReqVO reqVO) {
        validateLeafCategory(reqVO.getTargetCategoryId());

        List<CategoryAttributeRelation> templateRelations = getRelationsByScope(reqVO.getTemplateCategoryId(), reqVO.getScope());
        List<CategoryAttributeRelation> targetRelations = getRelationsByScope(reqVO.getTargetCategoryId(), reqVO.getScope());
        Set<Long> targetAttrIds = targetRelations.stream().map(CategoryAttributeRelation::getAttrId).collect(Collectors.toSet());

        List<CategoryAttributeRelation> toAdd = new ArrayList<>();
        List<CategoryAttributeRelation> toSkip = new ArrayList<>();
        for (CategoryAttributeRelation templateRel : templateRelations) {
            if (targetAttrIds.contains(templateRel.getAttrId())) {
                toSkip.add(templateRel);
            } else {
                toAdd.add(templateRel);
            }
        }

        List<CategoryAttributeRelation> toRemove = new ArrayList<>();
        if ("replace".equals(reqVO.getMode())) {
            Set<Long> templateAttrIds = templateRelations.stream().map(CategoryAttributeRelation::getAttrId).collect(Collectors.toSet());
            toRemove = targetRelations.stream()
                    .filter(relation -> !templateAttrIds.contains(relation.getAttrId()))
                    .toList();
        }

        Set<Long> allAttrIds = new HashSet<>();
        toAdd.forEach(relation -> allAttrIds.add(relation.getAttrId()));
        toRemove.forEach(relation -> allAttrIds.add(relation.getAttrId()));
        toSkip.forEach(relation -> allAttrIds.add(relation.getAttrId()));

        Map<Long, Attribute> attrMap = Collections.emptyMap();
        if (!allAttrIds.isEmpty()) {
            attrMap = attributeDao.selectByIds(new ArrayList<>(allAttrIds)).stream()
                    .collect(Collectors.toMap(Attribute::getId, attribute -> attribute));
        }

        AttributeTemplatePreviewRespVO vo = new AttributeTemplatePreviewRespVO();
        vo.setTraceId(IdUtil.fastSimpleUUID());
        vo.setAddAttrIds(toAdd.stream().map(CategoryAttributeRelation::getAttrId).toList());

        AttributeTemplatePreviewRespVO.TemplateSummary summary = new AttributeTemplatePreviewRespVO.TemplateSummary();
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
    public AttributeTemplateApplyRespVO templateApply(AttributeTemplateApplyReqVO reqVO) {
        validateLeafCategory(reqVO.getTargetCategoryId());

        List<CategoryAttributeRelation> templateRelations = getRelationsByScope(reqVO.getTemplateCategoryId(), reqVO.getScope());
        List<CategoryAttributeRelation> targetRelations = getRelationsByScope(reqVO.getTargetCategoryId(), reqVO.getScope());
        Set<Long> targetAttrIds = targetRelations.stream().map(CategoryAttributeRelation::getAttrId).collect(Collectors.toSet());

        int addCount = 0;
        int removeCount = 0;
        int skipCount = 0;

        if ("replace".equals(reqVO.getMode())) {
            Set<Long> templateAttrIds = templateRelations.stream().map(CategoryAttributeRelation::getAttrId).collect(Collectors.toSet());
            List<Long> toDeleteAttrIds = targetRelations.stream()
                    .map(CategoryAttributeRelation::getAttrId)
                    .filter(attrId -> !templateAttrIds.contains(attrId))
                    .toList();
            if (!toDeleteAttrIds.isEmpty()) {
                removeCount = relationDao.deleteByCategoryIdAndAttrIds(reqVO.getTargetCategoryId(), toDeleteAttrIds);
            }
        }

        List<CategoryAttributeRelation> toInsert = new ArrayList<>();
        for (CategoryAttributeRelation templateRel : templateRelations) {
            if (targetAttrIds.contains(templateRel.getAttrId())) {
                skipCount++;
            } else {
                if (CollUtil.isNotEmpty(reqVO.getSelectedAddAttrIds()) && !reqVO.getSelectedAddAttrIds().contains(templateRel.getAttrId())) {
                    skipCount++;
                    continue;
                }
                CategoryAttributeRelation newRel = new CategoryAttributeRelation();
                newRel.setCategoryId(reqVO.getTargetCategoryId());
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

        AttributeTemplateApplyRespVO vo = new AttributeTemplateApplyRespVO();
        vo.setTraceId(reqVO.getTraceId());
        vo.setSuccess(true);

        AttributeTemplatePreviewRespVO.TemplateSummary summary = new AttributeTemplatePreviewRespVO.TemplateSummary();
        summary.setAddCount(addCount);
        summary.setRemoveCount(removeCount);
        summary.setSkipCount(skipCount);
        vo.setSummary(summary);
        return vo;
    }

    private void validateLeafCategory(Long categoryId) {
        Map<Long, Long> childCountMap = categoryService.countChildrenByParentIds(List.of(categoryId));
        if (childCountMap.getOrDefault(categoryId, 0L) > 0) {
            throw new ApiException("目标必须是叶子分类");
        }
    }

    private List<CategoryAttributeRelation> getRelationsByScope(Long categoryId, String scope) {
        return switch (scope) {
            case "spec" -> relationDao.selectByCategoryIdAndType(categoryId, 1);
            case "param" -> relationDao.selectByCategoryIdAndType(categoryId, 0);
            case "both" -> relationDao.selectByCategoryId(categoryId);
            default -> throw new ApiException("无效的 scope: " + scope);
        };
    }

    private List<AttributeTemplatePreviewRespVO.TemplateAttrSample> buildSampleList(
            List<CategoryAttributeRelation> relations,
            Map<Long, Attribute> attrMap,
            int limit) {
        return relations.stream()
                .limit(limit)
                .map(relation -> {
                    Attribute attr = attrMap.get(relation.getAttrId());
                    AttributeTemplatePreviewRespVO.TemplateAttrSample sample = new AttributeTemplatePreviewRespVO.TemplateAttrSample();
                    sample.setAttrId(relation.getAttrId());
                    sample.setAttrName(attr != null ? attr.getName() : "未知");
                    sample.setType(attr != null ? attr.getType() : null);
                    sample.setGroupName(relation.getGroupName());
                    return sample;
                })
                .toList();
    }

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