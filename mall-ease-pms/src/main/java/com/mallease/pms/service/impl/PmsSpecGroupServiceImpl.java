package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.converter.PmsAttributeConverter;
import com.mallease.pms.dao.PmsCategorySpecGroupDao;
import com.mallease.pms.dao.PmsSpecDao;
import com.mallease.pms.dao.PmsSpecGroupDao;
import com.mallease.pms.dto.cmd.CreatePmsSpecGroupCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpecGroupCmd;
import com.mallease.pms.dto.vo.PmsSpecGroupVO;
import com.mallease.pms.dto.vo.PmsSpecVO;
import com.mallease.pms.pojo.PmsCategorySpecGroup;
import com.mallease.pms.pojo.PmsSpec;
import com.mallease.pms.pojo.PmsSpecGroup;
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
