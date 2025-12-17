package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.converter.PmsAttributeConverter;
import com.mallease.pms.dao.PmsCategoryParamGroupDao;
import com.mallease.pms.dao.PmsParamDao;
import com.mallease.pms.dao.PmsParamGroupDao;
import com.mallease.pms.dto.cmd.ClonePmsParamGroupCmd;
import com.mallease.pms.dto.cmd.CreatePmsParamGroupCmd;
import com.mallease.pms.dto.cmd.UpdatePmsParamGroupCmd;
import com.mallease.pms.dto.vo.PmsParamGroupVO;
import com.mallease.pms.pojo.PmsCategoryParamGroup;
import com.mallease.pms.pojo.PmsParam;
import com.mallease.pms.pojo.PmsParamGroup;
import com.mallease.pms.service.PmsParamGroupService;
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
 * 参数组服务实现类
 * <p>
 * 核心功能：
 * 1. 参数组 CRUD
 * 2. 分类关联管理（通过 pms_category_param_group 关联表）
 * 3. 参数列表填充
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Slf4j
@Service
public class PmsParamGroupServiceImpl implements PmsParamGroupService {

    @Autowired
    private PmsParamGroupDao paramGroupDao;

    @Autowired
    private PmsParamDao paramDao;

    @Autowired
    private PmsCategoryParamGroupDao categoryParamGroupDao;

    @Autowired
    private PmsAttributeConverter attributeConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreatePmsParamGroupCmd cmd) {
        // 检查名称是否重复
        PmsParamGroup existing = paramGroupDao.selectByName(cmd.getName());
        if (existing != null) {
            throw new ApiException("参数组名称已存在: " + cmd.getName());
        }

        PmsParamGroup entity = attributeConverter.createParamGroupCmdToEntity(cmd);
        paramGroupDao.insertSelective(entity);

        // 如果传了 categoryId，自动绑定到该分类
        if (cmd.getCategoryId() != null) {
            bindToCategory(cmd.getCategoryId(), List.of(entity.getId()));
            log.info("创建参数组成功并绑定到分类，ID: {}, 名称: {}, 分类ID: {}",
                    entity.getId(), entity.getName(), cmd.getCategoryId());
        } else {
            log.info("创建参数组成功，ID: {}, 名称: {}", entity.getId(), entity.getName());
        }
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdatePmsParamGroupCmd cmd) {
        PmsParamGroup original = paramGroupDao.selectByPrimaryKey(cmd.getId());
        if (original == null) {
            throw new ApiException("参数组不存在，ID: " + cmd.getId());
        }

        // 检查名称是否与其他参数组重复
        if (StringUtils.hasText(cmd.getName()) && !cmd.getName().equals(original.getName())) {
            PmsParamGroup existing = paramGroupDao.selectByName(cmd.getName());
            if (existing != null && !existing.getId().equals(cmd.getId())) {
                throw new ApiException("参数组名称已存在: " + cmd.getName());
            }
        }

        attributeConverter.updateParamGroupFromCmd(original, cmd);
        return paramGroupDao.updateByPrimaryKeySelective(original);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        PmsParamGroup paramGroup = paramGroupDao.selectByPrimaryKey(id);
        if (paramGroup == null) {
            return 0;
        }

        // 检查是否有关联的参数定义
        List<PmsParam> params = paramDao.selectByGroupId(id);
        if (!CollectionUtils.isEmpty(params)) {
            throw new ApiException("该参数组下存在参数定义，请先删除参数");
        }

        // 删除分类关联
        categoryParamGroupDao.deleteByParamGroupId(id);

        return paramGroupDao.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        // 检查是否有关联的参数定义
        List<PmsParam> params = paramDao.selectByGroupIds(ids);
        if (!CollectionUtils.isEmpty(params)) {
            Long groupId = params.get(0).getGroupId();
            PmsParamGroup group = paramGroupDao.selectByPrimaryKey(groupId);
            String groupName = group != null ? group.getName() : "未知";
            throw new ApiException("参数组「" + groupName + "」下存在参数定义，请先删除参数");
        }

        // 批量删除分类关联
        categoryParamGroupDao.deleteByParamGroupIds(ids);

        return paramGroupDao.deleteBatch(ids);
    }

    @Override
    public PmsParamGroupVO getById(Long id) {
        PmsParamGroup paramGroup = paramGroupDao.selectByPrimaryKey(id);
        if (paramGroup == null) {
            return null;
        }

        PmsParamGroupVO vo = attributeConverter.paramGroupToVo(paramGroup);

        // 填充参数列表
        List<PmsParam> params = paramDao.selectByGroupId(id);
        vo.setParamList(attributeConverter.paramListToVoList(params));
        vo.setParamCount(params.size());

        return vo;
    }

    @Override
    public List<PmsParamGroupVO> listAll() {
        List<PmsParamGroup> paramGroups = paramGroupDao.selectAll();
        return fillParamList(paramGroups);
    }

    @Override
    public List<PmsParamGroup> listEntities(String keyword) {
        return paramGroupDao.selectByKeyword(keyword);
    }

    @Override
    public List<PmsParamGroupVO> list(String keyword) {
        List<PmsParamGroup> paramGroups = paramGroupDao.selectByKeyword(keyword);
        return fillParamList(paramGroups);
    }

    @Override
    public List<PmsParamGroupVO> toVoListWithParams(List<PmsParamGroup> paramGroups) {
        return fillParamList(paramGroups);
    }

    @Override
    public List<PmsParamGroupVO> listByCategoryId(Long categoryId) {
        // 通过关联表查询参数组ID
        List<PmsCategoryParamGroup> relations = categoryParamGroupDao.selectByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(relations)) {
            return new ArrayList<>();
        }

        List<Long> paramGroupIds = relations.stream()
                .map(PmsCategoryParamGroup::getParamGroupId)
                .collect(Collectors.toList());

        List<PmsParamGroup> paramGroups = paramGroupDao.selectByIds(paramGroupIds);
        return fillParamList(paramGroups);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bindToCategory(Long categoryId, List<Long> paramGroupIds) {
        if (CollectionUtils.isEmpty(paramGroupIds)) {
            return 0;
        }

        // 过滤已存在的关联
        List<PmsCategoryParamGroup> existingRelations = categoryParamGroupDao.selectByCategoryId(categoryId);
        List<Long> existingGroupIds = existingRelations.stream()
                .map(PmsCategoryParamGroup::getParamGroupId)
                .collect(Collectors.toList());

        List<PmsCategoryParamGroup> newRelations = paramGroupIds.stream()
                .filter(groupId -> !existingGroupIds.contains(groupId))
                .map(groupId -> {
                    PmsCategoryParamGroup relation = new PmsCategoryParamGroup();
                    relation.setCategoryId(categoryId);
                    relation.setParamGroupId(groupId);
                    return relation;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(newRelations)) {
            return 0;
        }

        return categoryParamGroupDao.insertBatch(newRelations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unbindFromCategory(Long categoryId, List<Long> paramGroupIds) {
        if (CollectionUtils.isEmpty(paramGroupIds)) {
            return 0;
        }

        return categoryParamGroupDao.deleteByCategoryIdAndParamGroupIds(categoryId, paramGroupIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long cloneToCategory(ClonePmsParamGroupCmd cmd) {
        Long sourceGroupId = cmd.getParamGroupId();
        Long categoryId = cmd.getCategoryId();

        // 1. 查询原参数组
        PmsParamGroup sourceGroup = paramGroupDao.selectByPrimaryKey(sourceGroupId);
        if (sourceGroup == null) {
            throw new ApiException("参数组不存在，ID: " + sourceGroupId);
        }

        // 2. 创建新参数组（复制基本信息）
        String newName = StringUtils.hasText(cmd.getNewName())
                ? cmd.getNewName()
                : sourceGroup.getName() + "_副本";

        // 检查名称是否重复，如果重复则添加时间戳
        PmsParamGroup existingGroup = paramGroupDao.selectByName(newName);
        if (existingGroup != null) {
            newName = newName + "_" + System.currentTimeMillis();
        }

        PmsParamGroup newGroup = new PmsParamGroup();
        newGroup.setName(newName);
        newGroup.setSort(sourceGroup.getSort());
        newGroup.setStatus(sourceGroup.getStatus());
        paramGroupDao.insertSelective(newGroup);
        Long newGroupId = newGroup.getId();

        // 3. 复制参数定义
        List<PmsParam> sourceParams = paramDao.selectByGroupId(sourceGroupId);
        if (!CollectionUtils.isEmpty(sourceParams)) {
            List<PmsParam> newParams = new ArrayList<>();
            for (PmsParam sourceParam : sourceParams) {
                PmsParam newParam = new PmsParam();
                newParam.setGroupId(newGroupId);
                newParam.setName(sourceParam.getName());
                newParam.setUnit(sourceParam.getUnit());
                newParam.setInputType(sourceParam.getInputType());
                newParam.setInputList(sourceParam.getInputList());
                newParam.setIsRequired(sourceParam.getIsRequired());
                newParam.setIsSearchable(sourceParam.getIsSearchable());
                newParam.setIsHighlight(sourceParam.getIsHighlight());
                newParam.setIsComparable(sourceParam.getIsComparable());
                newParam.setSort(sourceParam.getSort());
                newParams.add(newParam);
            }

            // 批量插入新参数
            paramDao.insertBatch(newParams);
        }

        // 4. 解除当前分类与原参数组的关联
        unbindFromCategory(categoryId, List.of(sourceGroupId));

        // 5. 绑定新参数组到当前分类
        bindToCategory(categoryId, List.of(newGroupId));

        log.info("克隆参数组成功，原ID: {}, 新ID: {}, 分类ID: {}", sourceGroupId, newGroupId, categoryId);
        return newGroupId;
    }

    /**
     * 批量填充参数列表
     */
    private List<PmsParamGroupVO> fillParamList(List<PmsParamGroup> paramGroups) {
        if (CollectionUtils.isEmpty(paramGroups)) {
            return new ArrayList<>();
        }

        List<PmsParamGroupVO> voList = attributeConverter.paramGroupListToVoList(paramGroups);

        // 批量查询所有参数
        List<Long> groupIds = paramGroups.stream()
                .map(PmsParamGroup::getId)
                .collect(Collectors.toList());

        List<PmsParam> allParams = paramDao.selectByGroupIds(groupIds);

        // 按参数组ID分组
        Map<Long, List<PmsParam>> paramMap = allParams.stream()
                .collect(Collectors.groupingBy(PmsParam::getGroupId));

        // 填充参数列表和数量
        for (PmsParamGroupVO vo : voList) {
            List<PmsParam> params = paramMap.getOrDefault(vo.getId(), new ArrayList<>());
            vo.setParamList(attributeConverter.paramListToVoList(params));
            vo.setParamCount(params.size());
        }

        return voList;
    }
}
