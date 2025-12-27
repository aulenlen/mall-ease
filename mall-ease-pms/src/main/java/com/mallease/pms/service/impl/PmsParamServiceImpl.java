package com.mallease.pms.service.impl;

import com.mallease.common.exception.ApiException;
import com.mallease.pms.converter.PmsSpecConverter;
import com.mallease.pms.dao.PmsParamDao;
import com.mallease.pms.dao.PmsParamGroupDao;
import com.mallease.pms.dto.cmd.CreatePmsParamCmd;
import com.mallease.pms.dto.cmd.UpdatePmsParamCmd;
import com.mallease.pms.dto.vo.PmsParamVO;
import com.mallease.pms.pojo.PmsParam;
import com.mallease.pms.pojo.PmsParamGroup;
import com.mallease.pms.service.PmsParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 参数服务实现类
 * <p>
 * 核心功能：
 * 1. 参数定义 CRUD
 * 2. 特殊参数查询（可搜索、亮点、可对比）
 *
 * @author: Aulen
 * @create: 2025-12-16
 */
@Slf4j
@Service
public class PmsParamServiceImpl implements PmsParamService {

    @Autowired
    private PmsParamDao paramDao;

    @Autowired
    private PmsParamGroupDao paramGroupDao;

    @Autowired
    private PmsSpecConverter specConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreatePmsParamCmd cmd) {
        // 检查参数组是否存在
        PmsParamGroup paramGroup = paramGroupDao.selectByPrimaryKey(cmd.getGroupId());
        if (paramGroup == null) {
            throw new ApiException("参数组不存在，ID: " + cmd.getGroupId());
        }

        // 检查同一参数组下名称是否重复
        List<PmsParam> existingParams = paramDao.selectByGroupId(cmd.getGroupId());
        boolean nameExists = existingParams.stream()
                .anyMatch(p -> p.getName().equals(cmd.getName()));
        if (nameExists) {
            throw new ApiException("该参数组下已存在同名参数: " + cmd.getName());
        }

        // 创建参数
        PmsParam entity = specConverter.createParamCmdToEntity(cmd);
        paramDao.insertSelective(entity);

        log.info("创建参数成功，ID: {}, 名称: {}, 参数组: {}", entity.getId(), entity.getName(), paramGroup.getName());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdatePmsParamCmd cmd) {
        PmsParam original = paramDao.selectByPrimaryKey(cmd.getId());
        if (original == null) {
            throw new ApiException("参数不存在，ID: " + cmd.getId());
        }

        // 检查名称是否与同组其他参数重复
        if (StringUtils.hasText(cmd.getName()) && !cmd.getName().equals(original.getName())) {
            List<PmsParam> existingParams = paramDao.selectByGroupId(original.getGroupId());
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
        PmsParam param = paramDao.selectByPrimaryKey(id);
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
    public PmsParamVO getById(Long id) {
        PmsParam param = paramDao.selectByPrimaryKey(id);
        if (param == null) {
            return null;
        }

        PmsParamVO vo = specConverter.paramToVo(param);

        // 填充参数组名称
        PmsParamGroup paramGroup = paramGroupDao.selectByPrimaryKey(param.getGroupId());
        if (paramGroup != null) {
            vo.setGroupName(paramGroup.getName());
        }

        return vo;
    }

    @Override
    public List<PmsParamVO> listByGroupId(Long groupId) {
        List<PmsParam> params = paramDao.selectByGroupId(groupId);
        return specConverter.paramListToVoList(params);
    }

    @Override
    public List<PmsParamVO> listSearchable() {
        List<PmsParam> params = paramDao.selectSearchable();
        return specConverter.paramListToVoList(params);
    }

    @Override
    public List<PmsParamVO> listHighlight() {
        List<PmsParam> params = paramDao.selectHighlight();
        return specConverter.paramListToVoList(params);
    }

    @Override
    public List<PmsParamVO> listComparable() {
        List<PmsParam> params = paramDao.selectComparable();
        return specConverter.paramListToVoList(params);
    }
}
