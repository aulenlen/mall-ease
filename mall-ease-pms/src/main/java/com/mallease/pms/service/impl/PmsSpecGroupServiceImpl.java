package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.converter.PmsAttributeConverter;
import com.mallease.pms.dao.PmsCategorySpecGroupDao;
import com.mallease.pms.dao.PmsSpecDao;
import com.mallease.pms.dao.PmsSpecGroupDao;
import com.mallease.pms.dao.PmsSpecValueDao;
import com.mallease.pms.dto.cmd.ClonePmsSpecGroupCmd;
import com.mallease.pms.dto.cmd.CreatePmsSpecGroupCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpecGroupCmd;
import com.mallease.pms.dto.vo.PmsSpecGroupVO;
import com.mallease.pms.pojo.PmsCategorySpecGroup;
import com.mallease.pms.pojo.PmsSpec;
import com.mallease.pms.pojo.PmsSpecGroup;
import com.mallease.pms.pojo.PmsSpecValue;
import com.mallease.pms.service.PmsSpecGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 规格组服务实现类
 * <p>
 * 核心功能：
 * 1. 规格组 CRUD
 * 2. 分类关联管理（通过 pms_category_spec_group 关联表）
 * 3. 规格列表填充
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Slf4j
@Service
public class PmsSpecGroupServiceImpl implements PmsSpecGroupService {

    @Autowired
    private PmsSpecGroupDao specGroupDao;

    @Autowired
    private PmsSpecDao specDao;

    @Autowired
    private PmsCategorySpecGroupDao categorySpecGroupDao;

    @Autowired
    private PmsSpecValueDao specValueDao;

    @Autowired
    private PmsAttributeConverter attributeConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreatePmsSpecGroupCmd cmd) {
        // 检查名称是否重复
        PmsSpecGroup existing = specGroupDao.selectByName(cmd.getName());
        if (existing != null) {
            throw new ApiException("规格组名称已存在: " + cmd.getName());
        }

        PmsSpecGroup entity = attributeConverter.createSpecGroupCmdToEntity(cmd);
        specGroupDao.insertSelective(entity);

        // 如果传了 categoryId，自动绑定到该分类
        if (cmd.getCategoryId() != null) {
            bindToCategory(cmd.getCategoryId(), List.of(entity.getId()));
            log.info("创建规格组成功并绑定到分类，ID: {}, 名称: {}, 分类ID: {}",
                    entity.getId(), entity.getName(), cmd.getCategoryId());
        } else {
            log.info("创建规格组成功，ID: {}, 名称: {}", entity.getId(), entity.getName());
        }
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdatePmsSpecGroupCmd cmd) {
        PmsSpecGroup original = specGroupDao.selectByPrimaryKey(cmd.getId());
        if (original == null) {
            throw new ApiException("规格组不存在，ID: " + cmd.getId());
        }

        // 检查名称是否与其他规格组重复
        if (StringUtils.hasText(cmd.getName()) && !cmd.getName().equals(original.getName())) {
            PmsSpecGroup existing = specGroupDao.selectByName(cmd.getName());
            if (existing != null && !existing.getId().equals(cmd.getId())) {
                throw new ApiException("规格组名称已存在: " + cmd.getName());
            }
        }

        attributeConverter.updateSpecGroupFromCmd(original, cmd);
        return specGroupDao.updateByPrimaryKeySelective(original);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        PmsSpecGroup specGroup = specGroupDao.selectByPrimaryKey(id);
        if (specGroup == null) {
            return 0;
        }

        // 检查是否有关联的规格定义
        List<PmsSpec> specs = specDao.selectByGroupId(id);
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
        List<PmsSpec> specs = specDao.selectByGroupIds(ids);
        if (!CollectionUtils.isEmpty(specs)) {
            Long groupId = specs.get(0).getGroupId();
            PmsSpecGroup group = specGroupDao.selectByPrimaryKey(groupId);
            String groupName = group != null ? group.getName() : "未知";
            throw new ApiException("规格组「" + groupName + "」下存在规格定义，请先删除规格");
        }

        // 批量删除分类关联
        categorySpecGroupDao.deleteBySpecGroupIds(ids);

        return specGroupDao.deleteBatch(ids);
    }

    @Override
    public PmsSpecGroupVO getById(Long id) {
        PmsSpecGroup specGroup = specGroupDao.selectByPrimaryKey(id);
        if (specGroup == null) {
            return null;
        }

        PmsSpecGroupVO vo = attributeConverter.specGroupToVo(specGroup);

        // 填充规格列表
        List<PmsSpec> specs = specDao.selectByGroupId(id);
        vo.setSpecList(attributeConverter.specListToVoList(specs));
        vo.setSpecCount(specs.size());

        return vo;
    }

    @Override
    public List<PmsSpecGroupVO> listAll() {
        List<PmsSpecGroup> specGroups = specGroupDao.selectAll();
        return fillSpecList(specGroups);
    }

    @Override
    public List<PmsSpecGroup> listEntities(String keyword) {
        return specGroupDao.selectByKeyword(keyword);
    }

    @Override
    public List<PmsSpecGroupVO> list(String keyword) {
        List<PmsSpecGroup> specGroups = specGroupDao.selectByKeyword(keyword);
        return fillSpecList(specGroups);
    }

    @Override
    public List<PmsSpecGroupVO> toVoListWithSpecs(List<PmsSpecGroup> specGroups) {
        return fillSpecList(specGroups);
    }

    @Override
    public List<PmsSpecGroupVO> listByCategoryId(Long categoryId) {
        // 通过关联表查询规格组ID
        List<PmsCategorySpecGroup> relations = categorySpecGroupDao.selectByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(relations)) {
            return new ArrayList<>();
        }

        List<Long> specGroupIds = relations.stream()
                .map(PmsCategorySpecGroup::getSpecGroupId)
                .collect(Collectors.toList());

        List<PmsSpecGroup> specGroups = specGroupDao.selectByIds(specGroupIds);
        return fillSpecList(specGroups);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindToCategory(Long categoryId, List<Long> specGroupIds) {
        if (CollectionUtils.isEmpty(specGroupIds)) {
            return 0;
        }

        // 过滤已存在的关联
        List<PmsCategorySpecGroup> existingRelations = categorySpecGroupDao.selectByCategoryId(categoryId);
        List<Long> existingGroupIds = existingRelations.stream()
                .map(PmsCategorySpecGroup::getSpecGroupId)
                .collect(Collectors.toList());

        List<PmsCategorySpecGroup> newRelations = specGroupIds.stream()
                .filter(groupId -> !existingGroupIds.contains(groupId))
                .map(groupId -> {
                    PmsCategorySpecGroup relation = new PmsCategorySpecGroup();
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
    public Long cloneToCategory(ClonePmsSpecGroupCmd cmd) {
        Long sourceGroupId = cmd.getSpecGroupId();
        Long categoryId = cmd.getCategoryId();

        // 1. 查询原规格组
        PmsSpecGroup sourceGroup = specGroupDao.selectByPrimaryKey(sourceGroupId);
        if (sourceGroup == null) {
            throw new ApiException("规格组不存在，ID: " + sourceGroupId);
        }

        // 2. 创建新规格组（复制基本信息）
        String newName = StringUtils.hasText(cmd.getNewName())
                ? cmd.getNewName()
                : sourceGroup.getName() + "_副本";

        // 检查名称是否重复，如果重复则添加时间戳
        PmsSpecGroup existingGroup = specGroupDao.selectByName(newName);
        if (existingGroup != null) {
            newName = newName + "_" + System.currentTimeMillis();
        }

        PmsSpecGroup newGroup = new PmsSpecGroup();
        newGroup.setName(newName);
        newGroup.setSort(sourceGroup.getSort());
        newGroup.setStatus(sourceGroup.getStatus());
        specGroupDao.insertSelective(newGroup);
        Long newGroupId = newGroup.getId();

        // 3. 复制规格定义及规格值
        List<PmsSpec> sourceSpecs = specDao.selectByGroupId(sourceGroupId);
        if (!CollectionUtils.isEmpty(sourceSpecs)) {
            // 一次性查询所有规格值（避免循环查库）
            List<Long> sourceSpecIds = sourceSpecs.stream()
                    .map(PmsSpec::getId)
                    .collect(Collectors.toList());
            List<PmsSpecValue> allSourceValues = specValueDao.selectBySpecIds(sourceSpecIds);
            Map<Long, List<PmsSpecValue>> valuesBySpecId = allSourceValues.stream()
                    .collect(Collectors.groupingBy(PmsSpecValue::getSpecId));

            // 构建新规格列表，同时保存 sourceSpecId -> newSpec 的映射
            List<PmsSpec> newSpecs = new ArrayList<>();
            Map<Long, PmsSpec> sourceToNewSpecMap = new java.util.LinkedHashMap<>();

            for (PmsSpec sourceSpec : sourceSpecs) {
                PmsSpec newSpec = new PmsSpec();
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
            List<PmsSpecValue> allNewValues = new ArrayList<>();
            for (Map.Entry<Long, PmsSpec> entry : sourceToNewSpecMap.entrySet()) {
                Long sourceSpecId = entry.getKey();
                Long newSpecId = entry.getValue().getId();

                List<PmsSpecValue> sourceValues = valuesBySpecId.getOrDefault(sourceSpecId, new ArrayList<>());
                for (PmsSpecValue sv : sourceValues) {
                    PmsSpecValue newValue = new PmsSpecValue();
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

    /**
     * 批量填充规格列表
     * <p>
     * 避免 N+1 查询问题，一次性查询所有规格组的规格
     */
    private List<PmsSpecGroupVO> fillSpecList(List<PmsSpecGroup> specGroups) {
        if (CollectionUtils.isEmpty(specGroups)) {
            return new ArrayList<>();
        }

        List<PmsSpecGroupVO> voList = attributeConverter.specGroupListToVoList(specGroups);

        // 批量查询所有规格
        List<Long> groupIds = specGroups.stream()
                .map(PmsSpecGroup::getId)
                .collect(Collectors.toList());

        List<PmsSpec> allSpecs = specDao.selectByGroupIds(groupIds);

        // 按规格组ID分组
        Map<Long, List<PmsSpec>> specMap = allSpecs.stream()
                .collect(Collectors.groupingBy(PmsSpec::getGroupId));

        // 填充规格列表和数量
        for (PmsSpecGroupVO vo : voList) {
            List<PmsSpec> specs = specMap.getOrDefault(vo.getId(), new ArrayList<>());
            vo.setSpecList(attributeConverter.specListToVoList(specs));
            vo.setSpecCount(specs.size());
        }

        return voList;
    }
}
