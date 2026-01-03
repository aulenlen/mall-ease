package com.mallease.product.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.product.dao.CategorySpecGroupDao;
import com.mallease.product.dao.SpecDao;
import com.mallease.product.dao.SpecGroupDao;
import com.mallease.product.dao.SpecValueDao;
import com.mallease.product.model.client.cmd.SaveSpecGroupCmd;
import com.mallease.product.model.data.entity.CategorySpecGroup;
import com.mallease.product.model.data.entity.Spec;
import com.mallease.product.model.data.entity.SpecGroup;

import com.mallease.product.model.data.entity.SpecValue;
import com.mallease.product.service.SpecGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 规格组服务实现类
 * 核心功能：
 * 1. 规格组 CRUD
 * 2. 分类关联管理（通过 pms_category_spec_group 关联表）
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Slf4j
@Service
public class SpecGroupServiceImpl implements SpecGroupService {

    @Autowired
    private SpecGroupDao specGroupDao;
    @Autowired
    private SpecDao specDao;
    @Autowired
    private CategorySpecGroupDao categorySpecGroupDao;
    @Autowired
    private SpecValueDao specValueDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SpecGroup entity, Long categoryId) {

        // 检查名称是否重复
        SpecGroup existing = specGroupDao.selectByName(entity.getName());
        if (existing != null) {
            throw new ApiException("规格组名称已存在: " + entity.getName());
        }

        specGroupDao.insertSelective(entity);

        // 如果传了 categoryId，自动绑定到该分类
        if (categoryId != null) {
            bindToCategory(categoryId, List.of(entity.getId()));
            log.info("创建规格组成功并绑定到分类，ID: {}, 名称: {}, 分类ID: {}",
                    entity.getId(), entity.getName(), categoryId);
        } else {
            log.info("创建规格组成功，ID: {}, 名称: {}", entity.getId(), entity.getName());
        }
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(SpecGroup entity) {
        if (entity.getId() == null) {
            throw new ApiException("规格组ID不能为空");
        }

        SpecGroup original = specGroupDao.selectByPrimaryKey(entity.getId());
        if (original == null) {
            throw new ApiException("规格组不存在，ID: " + entity.getId());
        }

        // 检查名称是否与其他规格组重复
        if (StringUtils.hasText(entity.getName()) && !entity.getName().equals(original.getName())) {
            SpecGroup existingByName = specGroupDao.selectByName(entity.getName());
            if (existingByName != null && !existingByName.getId().equals(entity.getId())) {
                throw new ApiException("规格组名称已存在: " + entity.getName());
            }
        }

        return specGroupDao.updateByPrimaryKeySelective(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        SpecGroup specGroup = specGroupDao.selectByPrimaryKey(id);
        if (specGroup == null) {
            return 0;
        }

        // 检查是否有关联的规格定义
        List<Spec> specs = specDao.selectByGroupId(id);
        if (!CollectionUtils.isEmpty(specs)) {
            throw new ApiException("该规格组下存在规格定义，请先删除规格");
        }

        // 删除分类关联
        categorySpecGroupDao.deleteBySpecGroupId(id);
        return specGroupDao.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        // 检查是否有关联的规格定义
        List<Spec> specs = specDao.selectByGroupIds(ids);
        if (!CollectionUtils.isEmpty(specs)) {
            Long groupId = specs.get(0).getGroupId();
            SpecGroup group = specGroupDao.selectByPrimaryKey(groupId);
            String groupName = group != null ? group.getName() : "未知";
            throw new ApiException("规格组「" + groupName + "」下存在规格定义，请先删除规格");
        }

        // 批量删除分类关联
        categorySpecGroupDao.deleteBySpecGroupIds(ids);
        return specGroupDao.deleteBatch(ids);
    }

    @Override
    public SpecGroup getById(Long id) {
        return specGroupDao.selectByPrimaryKey(id);
    }

    @Override
    public List<SpecGroup> listAll() {
        return specGroupDao.selectAll();
    }

    @Override
    public List<SpecGroup> listEntities(String keyword) {
        return specGroupDao.selectByKeyword(keyword);
    }

    @Override
    public List<SpecGroup> list(String keyword) {
        return specGroupDao.selectByKeyword(keyword);
    }

    @Override
    public List<SpecGroup> listByCategoryId(Long categoryId) {

        // 通过关联表查询规格组ID
        List<CategorySpecGroup> relations = categorySpecGroupDao.selectByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(relations)) {
            return new ArrayList<>();
        }

        List<Long> specGroupIds = relations.stream()
                .map(CategorySpecGroup::getSpecGroupId)
                .collect(Collectors.toList());
        return specGroupDao.selectByIds(specGroupIds);
    }

    @Override
    public Map<Long, List<Spec>> getSpecsByGroupIds(List<Long> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return Map.of();
        }

        List<Spec> allSpecs = specDao.selectByGroupIds(groupIds);
        return allSpecs.stream()
                .collect(Collectors.groupingBy(Spec::getGroupId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindToCategory(Long categoryId, List<Long> specGroupIds) {
        if (CollectionUtils.isEmpty(specGroupIds)) {
            return 0;
        }

        // 过滤已存在的关联
        List<CategorySpecGroup> existingRelations = categorySpecGroupDao.selectByCategoryId(categoryId);
        List<Long> existingGroupIds = existingRelations.stream()
                .map(CategorySpecGroup::getSpecGroupId)
                .collect(Collectors.toList());
        List<CategorySpecGroup> newRelations = specGroupIds.stream()
                .filter(groupId -> !existingGroupIds.contains(groupId))
                .map(groupId -> {
                    CategorySpecGroup relation = new CategorySpecGroup();
                    relation.setCategoryId(categoryId);
                    relation.setSpecGroupId(groupId);
                    return relation;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(newRelations)) {
            return 0;
        }

        return categorySpecGroupDao.insertBatch(newRelations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unbindFromCategory(Long categoryId, List<Long> specGroupIds) {
        if (CollectionUtils.isEmpty(specGroupIds)) {
            return 0;
        }

        return categorySpecGroupDao.deleteByCategoryIdAndSpecGroupIds(categoryId, specGroupIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long cloneToCategory(SaveSpecGroupCmd cmd) {
        Long sourceGroupId = cmd.getId();
        Long categoryId = cmd.getCategoryId();

        // 1. 查询原规格组
        SpecGroup sourceGroup = specGroupDao.selectByPrimaryKey(sourceGroupId);
        if (sourceGroup == null) {
            throw new ApiException("规格组不存在，ID: " + sourceGroupId);
        }

        // 2. 创建新规格组（复制基本信息）
        String newName = StringUtils.hasText(cmd.getName())
                ? cmd.getName()
                : sourceGroup.getName() + "_副本";

        // 检查名称是否重复，如果重复则添加时间戳
        SpecGroup existingGroup = specGroupDao.selectByName(newName);
        if (existingGroup != null) {
            newName = newName + "_" + System.currentTimeMillis();
        }

        SpecGroup newGroup = new SpecGroup();
        newGroup.setName(newName);
        newGroup.setSort(sourceGroup.getSort());
        newGroup.setStatus(sourceGroup.getStatus());
        specGroupDao.insertSelective(newGroup);
        Long newGroupId = newGroup.getId();

        // 3. 复制规格定义及规格值
        List<Spec> sourceSpecs = specDao.selectByGroupId(sourceGroupId);
        if (!CollectionUtils.isEmpty(sourceSpecs)) {

            // 一次性查询所有规格值（避免循环查库）
            List<Long> sourceSpecIds = sourceSpecs.stream()
                    .map(Spec::getId)
                    .collect(Collectors.toList());
            List<SpecValue> allSourceValues = specValueDao.selectBySpecIds(sourceSpecIds);
            Map<Long, List<SpecValue>> valuesBySpecId = allSourceValues.stream()
                    .collect(Collectors.groupingBy(SpecValue::getSpecId));

            // 构建新规格列表，同时保存 sourceSpecId -> newSpec 的映射
            List<Spec> newSpecs = new ArrayList<>();
            Map<Long, Spec> sourceToNewSpecMap = new LinkedHashMap<>();
            for (Spec sourceSpec : sourceSpecs) {
                Spec newSpec = new Spec();
                newSpec.setGroupId(newGroupId);
                newSpec.setName(sourceSpec.getName());
                newSpec.setDisplayType(sourceSpec.getDisplayType());
                newSpec.setIsRequired(sourceSpec.getIsRequired());
                newSpec.setIsSearchable(sourceSpec.getIsSearchable());
                newSpec.setIsFilterable(sourceSpec.getIsFilterable());
                newSpec.setSort(sourceSpec.getSort());
                newSpecs.add(newSpec);
                sourceToNewSpecMap.put(sourceSpec.getId(), newSpec);
            }

            // 批量插入新规格（insertBatch 配置了 useGeneratedKeys，会自动填充ID）
            specDao.insertBatch(newSpecs);

            // 构建新规格值列表
            List<SpecValue> allNewValues = new ArrayList<>();
            for (Map.Entry<Long, Spec> entry : sourceToNewSpecMap.entrySet()) {
                Long sourceSpecId = entry.getKey();
                Long newSpecId = entry.getValue().getId();
                List<SpecValue> sourceValues = valuesBySpecId.getOrDefault(sourceSpecId, new ArrayList<>());
                for (SpecValue sv : sourceValues) {
                    SpecValue newValue = new SpecValue();
                    newValue.setSpecId(newSpecId);
                    newValue.setValue(sv.getValue());
                    newValue.setImage(sv.getImage());
                    newValue.setColorCode(sv.getColorCode());
                    newValue.setSort(sv.getSort());
                    allNewValues.add(newValue);
                }
            }

            // 批量插入所有规格值
            if (!CollectionUtils.isEmpty(allNewValues)) {
                specValueDao.insertBatch(allNewValues);
            }
        }

        // 4. 解除当前分类与原规格组的关联
        unbindFromCategory(categoryId, List.of(sourceGroupId));

        // 5. 绑定新规格组到当前分类
        bindToCategory(categoryId, List.of(newGroupId));
        log.info("克隆规格组成功，原ID: {}, 新ID: {}, 分类ID: {}", sourceGroupId, newGroupId, categoryId);
        return newGroupId;
    }

    @Override
    public List<SpecValue> listSpecValuesByCategoryId(Long categoryId) {

        // 1. 查询分类关联的规格组ID
        List<CategorySpecGroup> relations = categorySpecGroupDao.selectByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(relations)) {
            return new ArrayList<>();
        }

        List<Long> specGroupIds = relations.stream()
                .map(CategorySpecGroup::getSpecGroupId)
                .collect(Collectors.toList());

        // 2. 根据规格组ID查询规格
        List<Spec> specList = specDao.selectByGroupIds(specGroupIds);
        if (CollectionUtils.isEmpty(specList)) {
            return new ArrayList<>();
        }

        // 3. 根据规格ID查询规格值
        List<Long> specIds = specList.stream().map(Spec::getId).toList();
        return specValueDao.selectBySpecIds(specIds);
    }
}