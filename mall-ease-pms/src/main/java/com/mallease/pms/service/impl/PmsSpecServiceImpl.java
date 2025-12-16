package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.converter.PmsAttributeConverter;
import com.mallease.pms.dao.PmsSpecDao;
import com.mallease.pms.dao.PmsSpecGroupDao;
import com.mallease.pms.dao.PmsSpecValueDao;
import com.mallease.pms.dto.cmd.CreatePmsSpecCmd;
import com.mallease.pms.dto.cmd.UpdatePmsSpecCmd;
import com.mallease.pms.dto.vo.PmsSpecVO;
import com.mallease.pms.dto.vo.PmsSpecValueVO;
import com.mallease.pms.pojo.PmsSpec;
import com.mallease.pms.pojo.PmsSpecGroup;
import com.mallease.pms.pojo.PmsSpecValue;
import com.mallease.pms.service.PmsSpecService;
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
 * 规格服务实现类
 * <p>
 * 核心功能：
 * 1. 规格定义 CRUD
 * 2. 规格值管理
 * 3. 批量填充规格值（避免 N+1 查询）
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Slf4j
@Service
public class PmsSpecServiceImpl implements PmsSpecService {

    @Autowired
    private PmsSpecDao specDao;

    @Autowired
    private PmsSpecGroupDao specGroupDao;

    @Autowired
    private PmsSpecValueDao specValueDao;

    @Autowired
    private PmsAttributeConverter attributeConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreatePmsSpecCmd cmd) {
        // 检查规格组是否存在
        PmsSpecGroup specGroup = specGroupDao.selectByPrimaryKey(cmd.getGroupId());
        if (specGroup == null) {
            throw new ApiException("规格组不存在，ID: " + cmd.getGroupId());
        }

        // 检查同一规格组下名称是否重复
        List<PmsSpec> existingSpecs = specDao.selectByGroupId(cmd.getGroupId());
        boolean nameExists = existingSpecs.stream()
                .anyMatch(s -> s.getName().equals(cmd.getName()));
        if (nameExists) {
            throw new ApiException("该规格组下已存在同名规格: " + cmd.getName());
        }

        // 创建规格
        PmsSpec entity = attributeConverter.createSpecCmdToEntity(cmd);
        specDao.insertSelective(entity);

        // 创建规格值（如果有）
        if (!CollectionUtils.isEmpty(cmd.getValueList())) {
            List<PmsSpecValue> specValues = cmd.getValueList().stream()
                    .map(valueCmd -> {
                        PmsSpecValue value = attributeConverter.specValueCmdToEntity(valueCmd);
                        value.setSpecId(entity.getId());
                        return value;
                    })
                    .collect(Collectors.toList());
            specValueDao.insertBatch(specValues);
        }

        log.info("创建规格成功，ID: {}, 名称: {}, 规格组: {}", entity.getId(), entity.getName(), specGroup.getName());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdatePmsSpecCmd cmd) {
        PmsSpec original = specDao.selectByPrimaryKey(cmd.getId());
        if (original == null) {
            throw new ApiException("规格不存在，ID: " + cmd.getId());
        }

        // 检查名称是否与同组其他规格重复
        if (StringUtils.hasText(cmd.getName()) && !cmd.getName().equals(original.getName())) {
            List<PmsSpec> existingSpecs = specDao.selectByGroupId(original.getGroupId());
            boolean nameExists = existingSpecs.stream()
                    .filter(s -> !s.getId().equals(cmd.getId()))
                    .anyMatch(s -> s.getName().equals(cmd.getName()));
            if (nameExists) {
                throw new ApiException("该规格组下已存在同名规格: " + cmd.getName());
            }
        }

        attributeConverter.updateSpecFromCmd(original, cmd);
        return specDao.updateByPrimaryKeySelective(original);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        PmsSpec spec = specDao.selectByPrimaryKey(id);
        if (spec == null) {
            return 0;
        }

        // 删除关联的规格值
        specValueDao.deleteBySpecId(id);

        return specDao.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        // 批量删除关联的规格值
        specValueDao.deleteBySpecIds(ids);

        return specDao.deleteBatch(ids);
    }

    @Override
    public PmsSpecVO getById(Long id) {
        PmsSpec spec = specDao.selectByPrimaryKey(id);
        if (spec == null) {
            return null;
        }

        PmsSpecVO vo = attributeConverter.specToVo(spec);

        // 填充规格值列表
        List<PmsSpecValue> specValues = specValueDao.selectBySpecId(id);
        vo.setValueList(attributeConverter.specValueListToVoList(specValues));

        // 填充规格组名称
        PmsSpecGroup specGroup = specGroupDao.selectByPrimaryKey(spec.getGroupId());
        if (specGroup != null) {
            vo.setGroupName(specGroup.getName());
        }

        return vo;
    }

    @Override
    public List<PmsSpecVO> listByGroupId(Long groupId) {
        List<PmsSpec> specs = specDao.selectByGroupId(groupId);
        return fillSpecValues(specs);
    }

    @Override
    public List<PmsSpecVO> listSearchable() {
        List<PmsSpec> specs = specDao.selectSearchable();
        return fillSpecValues(specs);
    }

    @Override
    public List<PmsSpecVO> listFilterable() {
        List<PmsSpec> specs = specDao.selectFilterable();
        return fillSpecValues(specs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSpecValue(Long specId, CreatePmsSpecCmd.SpecValueCmd valueCmd) {
        PmsSpec spec = specDao.selectByPrimaryKey(specId);
        if (spec == null) {
            throw new ApiException("规格不存在，ID: " + specId);
        }

        // 检查规格值是否重复
        PmsSpecValue existing = specValueDao.selectBySpecIdAndValue(specId, valueCmd.getValue());
        if (existing != null) {
            throw new ApiException("该规格下已存在相同的规格值: " + valueCmd.getValue());
        }

        PmsSpecValue entity = attributeConverter.specValueCmdToEntity(valueCmd);
        entity.setSpecId(specId);
        specValueDao.insertSelective(entity);

        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addSpecValueBatch(Long specId, List<CreatePmsSpecCmd.SpecValueCmd> valueCmds) {
        if (CollectionUtils.isEmpty(valueCmds)) {
            return 0;
        }

        PmsSpec spec = specDao.selectByPrimaryKey(specId);
        if (spec == null) {
            throw new ApiException("规格不存在，ID: " + specId);
        }

        List<PmsSpecValue> specValues = valueCmds.stream()
                .map(valueCmd -> {
                    PmsSpecValue value = attributeConverter.specValueCmdToEntity(valueCmd);
                    value.setSpecId(specId);
                    return value;
                })
                .collect(Collectors.toList());

        return specValueDao.insertBatch(specValues);
    }

    @Override
    public int deleteSpecValue(Long valueId) {
        return specValueDao.deleteById(valueId);
    }

    @Override
    public int deleteSpecValueBatch(List<Long> valueIds) {
        if (CollectionUtils.isEmpty(valueIds)) {
            return 0;
        }
        return specValueDao.deleteBatch(valueIds);
    }

    @Override
    public List<PmsSpecValueVO> listSpecValuesBySpecId(Long specId) {
        List<PmsSpecValue> specValues = specValueDao.selectBySpecId(specId);
        return attributeConverter.specValueListToVoList(specValues);
    }

    /**
     * 批量填充规格值列表
     * <p>
     * 避免 N+1 查询问题，一次性查询所有规格的规格值
     */
    private List<PmsSpecVO> fillSpecValues(List<PmsSpec> specs) {
        if (CollectionUtils.isEmpty(specs)) {
            return new ArrayList<>();
        }

        List<PmsSpecVO> voList = attributeConverter.specListToVoList(specs);

        // 批量查询所有规格值
        List<Long> specIds = specs.stream()
                .map(PmsSpec::getId)
                .collect(Collectors.toList());

        List<PmsSpecValue> allValues = specValueDao.selectBySpecIds(specIds);

        // 按规格ID分组
        Map<Long, List<PmsSpecValue>> valueMap = allValues.stream()
                .collect(Collectors.groupingBy(PmsSpecValue::getSpecId));

        // 填充规格值列表
        for (PmsSpecVO vo : voList) {
            List<PmsSpecValue> values = valueMap.getOrDefault(vo.getId(), new ArrayList<>());
            vo.setValueList(attributeConverter.specValueListToVoList(values));
        }

        return voList;
    }
}
