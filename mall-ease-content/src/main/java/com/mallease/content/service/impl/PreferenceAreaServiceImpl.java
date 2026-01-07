package com.mallease.content.service.impl;

import com.mallease.content.dao.PreferenceAreaDao;
import com.mallease.content.dao.PreferenceAreaSpuRelationDao;
import com.mallease.content.model.data.entity.PreferenceArea;
import com.mallease.content.model.data.entity.PreferenceAreaSpuRelation;
import com.mallease.content.service.PreferenceAreaService;
import com.mallease.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 优选专区管理 Service 实现类
 *
 * @author: Aulen
 * @create: 2025-11-13
 */
@Slf4j
@Service
public class PreferenceAreaServiceImpl implements PreferenceAreaService {

    @Autowired
    private PreferenceAreaDao preferenceAreaDao;

    @Autowired
    private PreferenceAreaSpuRelationDao preferenceAreaSpuRelationDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(PreferenceArea preferenceArea) {
        return preferenceAreaDao.insertSelective(preferenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Long id, PreferenceArea preferenceArea) {
        preferenceArea.setId(id);
        return preferenceAreaDao.updateByPrimaryKeySelective(preferenceArea);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Long id) {
        return preferenceAreaDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatch(List<Long> ids) {
        return preferenceAreaDao.deleteBatch(ids);
    }

    @Override
    public PreferenceArea getById(Long id) {
        return preferenceAreaDao.selectByPrimaryKey(id);
    }

    @Override
    public List<PreferenceArea> listAll() {
        return preferenceAreaDao.selectAll();
    }

    @Override
    public List<PreferenceArea> listByName(String name) {
        return preferenceAreaDao.selectByName(name);
    }

    @Override
    public List<PreferenceArea> listByShowStatus(Integer showStatus) {
        return preferenceAreaDao.selectByShowStatus(showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateShowStatusBatch(List<Long> ids, Integer showStatus) {
        return preferenceAreaDao.updateShowStatusBatch(ids, showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAddSpuRelation(List<PreferenceAreaSpuRelation> relationList) {
        if (relationList == null || relationList.isEmpty()) {
            throw new ApiException("关联列表不能为空");
        }
        // 验证每条关联数据
        for (PreferenceAreaSpuRelation relation : relationList) {
            if (relation.getPreferenceAreaId() == null) {
                throw new ApiException("优选专区ID不能为空");
            }
            if (relation.getSpuId() == null) {
                throw new ApiException("商品ID不能为空");
            }
        }
        return preferenceAreaSpuRelationDao.insertBatch(relationList);
    }

    @Override
    public List<PreferenceAreaSpuRelation> getRelationsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return preferenceAreaSpuRelationDao.selectBySpuId(spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRelationsBySpuId(Long spuId) {
        if (spuId == null) {
            throw new ApiException("商品ID不能为空");
        }
        return preferenceAreaSpuRelationDao.deleteBySpuId(spuId);
    }
}
