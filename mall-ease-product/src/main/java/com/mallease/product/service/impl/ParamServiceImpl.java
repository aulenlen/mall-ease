package com.mallease.product.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.product.converter.SpecConverter;
import com.mallease.product.dao.ParamDao;
import com.mallease.product.dao.ParamGroupDao;
import com.mallease.product.model.client.cmd.ParamCmd;
import com.mallease.product.model.client.vo.ParamVO;
import com.mallease.product.model.data.entity.Param;
import com.mallease.product.model.data.entity.ParamGroup;
import com.mallease.product.service.ParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 参数服务实现类
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Slf4j
@Service
public class ParamServiceImpl implements ParamService {

    @Autowired
    private ParamDao paramDao;
    @Autowired
    private ParamGroupDao paramGroupDao;
    @Autowired
    private SpecConverter specConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ParamCmd cmd) {
        // 检查参数组是否存在
        ParamGroup paramGroup = paramGroupDao.selectByPrimaryKey(cmd.getGroupId());
        if (paramGroup == null) {
            throw new ApiException("参数组不存在，ID: " + cmd.getGroupId());
        }

        // 检查同一参数组下名称是否重复
        List<Param> existingParams = paramDao.selectByGroupId(cmd.getGroupId());
        boolean nameExists = existingParams.stream()
                .anyMatch(p -> p.getName().equals(cmd.getName()));
        if (nameExists) {
            throw new ApiException("该参数组下已存在同名参数: " + cmd.getName());
        }

        // 创建参数
        Param entity = specConverter.saveParamCmdToEntity(cmd);
        paramDao.insertSelective(entity);
        log.info("创建参数成功，ID: {}, 名称: {}, 参数组: {}", entity.getId(), entity.getName(), paramGroup.getName());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(ParamCmd cmd) {
        Param original = paramDao.selectByPrimaryKey(cmd.getId());
        if (original == null) {
            throw new ApiException("参数不存在，ID: " + cmd.getId());
        }

        // 检查名称是否与同组其他参数重复
        if (StringUtils.hasText(cmd.getName()) && !cmd.getName().equals(original.getName())) {
            List<Param> existingParams = paramDao.selectByGroupId(original.getGroupId());
            boolean nameExists = existingParams.stream()
                    .filter(p -> !p.getId().equals(cmd.getId()))
                    .anyMatch(p -> p.getName().equals(cmd.getName()));
            if (nameExists) {
                throw new ApiException("该参数组下已存在同名参数: " + cmd.getName());
            }
        }

        specConverter.updateParamFromCmd(original, cmd);
        return paramDao.updateByPrimaryKeySelective(original);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        Param param = paramDao.selectByPrimaryKey(id);
        if (param == null) {
            return 0;
        }

        return paramDao.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        return paramDao.deleteBatch(ids);
    }

    @Override
    public ParamVO getById(Long id) {
        Param param = paramDao.selectByPrimaryKey(id);
        if (param == null) {
            return null;
        }

        ParamVO vo = specConverter.paramToVo(param);

        // 填充参数组名称
        ParamGroup paramGroup = paramGroupDao.selectByPrimaryKey(param.getGroupId());
        if (paramGroup != null) {
            vo.setGroupName(paramGroup.getName());
        }

        return vo;
    }

    @Override
    public List<ParamVO> listByGroupId(Long groupId) {
        List<Param> params = paramDao.selectByGroupId(groupId);
        return specConverter.paramListToVoList(params);
    }

    @Override
    public List<ParamVO> listSearchable() {
        List<Param> params = paramDao.selectSearchable();
        return specConverter.paramListToVoList(params);
    }

    @Override
    public List<ParamVO> listHighlight() {
        List<Param> params = paramDao.selectHighlight();
        return specConverter.paramListToVoList(params);
    }

    @Override
    public List<ParamVO> listComparable() {
        List<Param> params = paramDao.selectComparable();
        return specConverter.paramListToVoList(params);
    }
}
