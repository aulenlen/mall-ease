package com.mallease.product.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.product.converter.SpecConverter;
import com.mallease.product.dao.SpecDao;
import com.mallease.product.dao.SpecGroupDao;
import com.mallease.product.dao.SpecValueDao;
import com.mallease.product.model.client.cmd.SaveSpecCmd;
import com.mallease.product.model.client.vo.SpecVO;
import com.mallease.product.model.data.entity.Spec;
import com.mallease.product.model.data.entity.SpecGroup;

import com.mallease.product.model.data.entity.SpecValue;
import com.mallease.product.service.SpecService;
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
 * 
 * @author: Aulen
 * @create: 2025-12-16
 */
@Slf4j
@Service
public class SpecServiceImpl implements SpecService {

    @Autowired
    private SpecDao specDao;
    @Autowired
    private SpecGroupDao specGroupDao;
    @Autowired
    private SpecValueDao specValueDao;
    @Autowired
    private SpecConverter specConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SaveSpecCmd cmd) {
        // 检查规格组是否存在
        SpecGroup specGroup = specGroupDao.selectByPrimaryKey(cmd.getGroupId());
        if (specGroup == null) {
            throw new ApiException("规格组不存在，ID: " + cmd.getGroupId());
        }

        // 检查同一规格组下名称是否重复
        List<Spec> existingSpecs = specDao.selectByGroupId(cmd.getGroupId());
        boolean nameExists = existingSpecs.stream()
                .anyMatch(s -> s.getName().equals(cmd.getName()));
        if (nameExists) {
            throw new ApiException("该规格组下已存在同名规格: " + cmd.getName());
        }

        // 创建规格
        Spec entity = specConverter.saveSpecCmdToEntity(cmd);
        specDao.insertSelective(entity);

        // 创建规格值（如果有）
        if (!CollectionUtils.isEmpty(cmd.getValueList())) {
            List<SpecValue> specValues = cmd.getValueList().stream()
                    .map(valueCmd -> {
                        SpecValue value = specConverter.specValueCmdToEntity(valueCmd);
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
    public int update(SaveSpecCmd cmd) {
        Spec original = specDao.selectByPrimaryKey(cmd.getId());
        if (original == null) {
            throw new ApiException("规格不存在，ID: " + cmd.getId());
        }

        // 检查名称是否与同组其他规格重复
        if (StringUtils.hasText(cmd.getName()) && !cmd.getName().equals(original.getName())) {
            List<Spec> existingSpecs = specDao.selectByGroupId(original.getGroupId());
            boolean nameExists = existingSpecs.stream()
                    .filter(s -> !s.getId().equals(cmd.getId()))
                    .anyMatch(s -> s.getName().equals(cmd.getName()));
            if (nameExists) {
                throw new ApiException("该规格组下已存在同名规格: " + cmd.getName());
            }
        }

        specConverter.updateSpecFromCmd(original, cmd);
        return specDao.updateByPrimaryKeySelective(original);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        Spec spec = specDao.selectByPrimaryKey(id);
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
    public SpecVO getById(Long id) {
        Spec spec = specDao.selectByPrimaryKey(id);
        if (spec == null) {
            return null;
        }

        SpecVO vo = specConverter.specToVo(spec);

        // 填充规格值列表
        List<SpecValue> specValues = specValueDao.selectBySpecId(id);
        vo.setValueList(specConverter.specValueListToVoList(specValues));

        // 填充规格组名称
        SpecGroup specGroup = specGroupDao.selectByPrimaryKey(spec.getGroupId());
        if (specGroup != null) {
            vo.setGroupName(specGroup.getName());
        }

        return vo;
    }

    @Override
    public List<SpecVO> listByGroupId(Long groupId) {
        List<Spec> specs = specDao.selectByGroupId(groupId);
        return fillSpecValues(specs);
    }

    @Override
    public List<SpecVO> listSearchable() {
        List<Spec> specs = specDao.selectSearchable();
        return fillSpecValues(specs);
    }

    @Override
    public List<SpecVO> listFilterable() {
        List<Spec> specs = specDao.selectFilterable();
        return fillSpecValues(specs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSpecValue(Long specId, SaveSpecCmd.SpecValueCmd valueCmd) {
        Spec spec = specDao.selectByPrimaryKey(specId);
        if (spec == null) {
            throw new ApiException("规格不存在，ID: " + specId);
        }

        // 检查规格值是否重复
        SpecValue existing = specValueDao.selectBySpecIdAndValue(specId, valueCmd.getValue());
        if (existing != null) {
            throw new ApiException("该规格下已存在相同的规格值: " + valueCmd.getValue());
        }

        SpecValue entity = specConverter.specValueCmdToEntity(valueCmd);
        entity.setSpecId(specId);
        specValueDao.insertSelective(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addSpecValueBatch(Long specId, List<SaveSpecCmd.SpecValueCmd> valueCmds) {
        if (CollectionUtils.isEmpty(valueCmds)) {
            return 0;
        }

        Spec spec = specDao.selectByPrimaryKey(specId);
        if (spec == null) {
            throw new ApiException("规格不存在，ID: " + specId);
        }

        List<SpecValue> specValues = valueCmds.stream()
                .map(valueCmd -> {
                    SpecValue value = specConverter.specValueCmdToEntity(valueCmd);
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
    public List<SpecValue> listSpecValuesBySpecId(Long specId) {
        return specValueDao.selectBySpecId(specId);
    }

    /**
     * 批量填充规格值列表
     */
    private List<SpecVO> fillSpecValues(List<Spec> specs) {
        if (CollectionUtils.isEmpty(specs)) {
            return new ArrayList<>();
        }

        List<SpecVO> voList = specConverter.specListToVoList(specs);

        // 批量查询所有规格值
        List<Long> specIds = specs.stream()
                .map(Spec::getId)
                .collect(Collectors.toList());
        List<SpecValue> allValues = specValueDao.selectBySpecIds(specIds);

        // 按规格ID分组
        Map<Long, List<SpecValue>> valueMap = allValues.stream()
                .collect(Collectors.groupingBy(SpecValue::getSpecId));

        // 填充规格值列表
        for (SpecVO vo : voList) {
            List<SpecValue> values = valueMap.getOrDefault(vo.getId(), new ArrayList<>());
            vo.setValueList(specConverter.specValueListToVoList(values));
        }

        return voList;
    }
}
